// Copyright (C) 2005 - 2008 Philip Aston
// All rights reserved.
//
// This file is part of The Grinder software distribution. Refer to
// the file LICENSE which is part of The Grinder distribution for
// licensing details. The Grinder distribution is available on the
// Internet at http://grinder.sourceforge.net/
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package org.bench4Q.console.distribution;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.bench4Q.agent.messages.CacheHighWaterMark;
import org.bench4Q.common.communication.Address;
import org.bench4Q.common.util.Directory;
import org.bench4Q.console.communication.ProcessControl;
import org.bench4Q.console.communication.ProcessControl.ProcessReports;
import org.bench4Q.console.messages.AgentAddress;
import org.bench4Q.console.messages.AgentAndCacheReport;

/**
 * {@link AgentCacheState} implementation.
 * 
 * @author Philip Aston
 * @version $Revision: 3878 $
 */
final class AgentCacheStateImplementation implements UpdateableAgentCacheState {

	private final PropertyChangeSupport m_propertyChangeSupport = new PropertyChangeSupport(this);

	// All mutable fields are guarded by this.
	private CacheParameters m_cacheParameters;
	private long m_latestNewFileTime = -1;

	private boolean m_outOfDate = false;
	private Set m_lastAgentReportSet = new HashSet();
	private long m_earliestAgentTime = -1;

	public AgentCacheStateImplementation(ProcessControl processControl, Directory directory,
			Pattern fileFilterPattern) {
		reset(new CacheParametersImplementation(directory, fileFilterPattern));
		processControl.addProcessStatusListener(new ProcessReportListener());
	}

	private synchronized void reset(CacheParameters cacheParameters) {
		if (!cacheParameters.equals(m_cacheParameters)) {
			m_cacheParameters = cacheParameters;
			m_latestNewFileTime = -1;

			m_lastAgentReportSet = new HashSet();
			m_earliestAgentTime = -1;
		}
	}

	public synchronized boolean getOutOfDate() {
		return m_outOfDate;
	}

	public synchronized void setNewFileTime(long time) {
		// Listeners will be updated on next report cycle.
		m_latestNewFileTime = Math.max(m_latestNewFileTime, time);
	}

	public void addListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void setDirectory(Directory directory) {
		reset(new CacheParametersImplementation(directory, m_cacheParameters.getFileFilterPattern()));
	}

	public synchronized void setFileFilterPattern(Pattern fileFilterPattern) {
		reset(new CacheParametersImplementation(m_cacheParameters.getDirectory(), fileFilterPattern));
	}

	public synchronized CacheParameters getCacheParameters() {
		return m_cacheParameters;
	}

	public synchronized AgentSet getAgentSet() {

		synchronized (this) {
			return new AgentSetImplementation(getCacheParameters(), m_lastAgentReportSet,
					m_earliestAgentTime);
		}
	}

	private final class AgentSetImplementation implements AgentSet {

		private final CacheParameters m_validCacheParameters;
		private final Set m_agentReports;
		private final long m_earliestAgentTime;

		private AgentSetImplementation(CacheParameters cacheParameters, Set agentReports,
				long earliestAgentTime) {
			m_validCacheParameters = cacheParameters;
			m_agentReports = agentReports;
			m_earliestAgentTime = earliestAgentTime;
		}

		private void checkValidity() throws OutOfDateException {
			if (!m_validCacheParameters.equals(getCacheParameters())) {
				throw new OutOfDateException();
			}
		}

		public Address getAddressOfAllAgents() throws OutOfDateException {
			checkValidity();

			final Set agentAddresses = new HashSet();
			final Iterator iterator = m_agentReports.iterator();

			while (iterator.hasNext()) {
				agentAddresses.add(new AgentAddress(((AgentAndCacheReport) iterator.next())
						.getAgentIdentity()));
			}

			return new AddressSet(agentAddresses);
		}

		public Address getAddressOfOutOfDateAgents(long time) throws OutOfDateException {
			checkValidity();

			final CacheHighWaterMark cacheState = m_validCacheParameters.createHighWaterMark(time);

			final Set outOfDateAgentAddresses = new HashSet();
			final Iterator iterator = m_agentReports.iterator();

			while (iterator.hasNext()) {
				final AgentAndCacheReport agentReport = (AgentAndCacheReport) iterator.next();

				final CacheHighWaterMark agentCache = agentReport.getCacheHighWaterMark();

				if (cacheState.isForSameCache(agentCache)) {
					if (cacheState.getTime() > agentCache.getTime()) {
						outOfDateAgentAddresses
								.add(new AgentAddress(agentReport.getAgentIdentity()));
					}
				} else {
					outOfDateAgentAddresses.add(new AgentAddress(agentReport.getAgentIdentity()));
				}
			}

			return new AddressSet(outOfDateAgentAddresses);
		}

		public long getEarliestAgentTime() {
			return m_earliestAgentTime;
		}
	}

	private final class ProcessReportListener implements ProcessControl.Listener {
		public void update(ProcessReports[] processReports) {

			final Set agents = new HashSet();

			final CacheHighWaterMark cacheState;

			synchronized (AgentCacheStateImplementation.this) {
				cacheState = m_cacheParameters.createHighWaterMark(m_latestNewFileTime);
			}

			long earliestAgentTime = Long.MAX_VALUE;

			for (int i = 0; i < processReports.length; ++i) {
				final AgentAndCacheReport agentReport = processReports[i].getAgentProcessReport();

				final CacheHighWaterMark agentCache = agentReport.getCacheHighWaterMark();

				if (cacheState.isForSameCache(agentCache)) {
					if (cacheState.getTime() > agentCache.getTime()) {
						earliestAgentTime = Math.min(earliestAgentTime, agentCache.getTime());
					}
				} else {
					earliestAgentTime = -1;
				}

				agents.add(agentReport);
			}

			final boolean oldOutOfDate;
			final boolean newOutOfDate;

			synchronized (AgentCacheStateImplementation.this) {
				m_lastAgentReportSet = Collections.unmodifiableSet(agents);
				m_earliestAgentTime = earliestAgentTime;

				oldOutOfDate = m_outOfDate;
				newOutOfDate = earliestAgentTime < Long.MAX_VALUE;
				m_outOfDate = newOutOfDate;
			}

			m_propertyChangeSupport.firePropertyChange("outOfDate", oldOutOfDate, newOutOfDate);
		}
	}

	private static final class AddressSet implements Address {
		private final Set m_addresses;

		public AddressSet(Set addresses) {
			m_addresses = addresses;
		}

		public boolean includes(Address address) {
			return m_addresses.contains(address);
		}
	}
}
