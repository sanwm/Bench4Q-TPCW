// Copyright (C) 2008 Philip Aston
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

package org.bench4Q.agent.common;

import org.bench4Q.common.Bench4QProperties;
import org.bench4Q.common.communication.CommunicationDefaults;
import org.bench4Q.common.communication.ConnectionType;
import org.bench4Q.common.communication.Connector;


public class ConnectorFactory {

	private final ConnectionType m_connectionType;

	/**
	 * Constructor.
	 * 
	 * @param connectionType
	 *            The connection type.
	 */
	public ConnectorFactory(ConnectionType connectionType) {
		m_connectionType = connectionType;
	}

	/**
	 * Factory method.
	 * 
	 * @param properties
	 *            Properties.
	 * @return A connector which can be used to contact the console.
	 */
	public Connector create(Bench4QProperties properties) {
		return new Connector(properties.getProperty(Bench4QProperties.CONSOLE_HOST,
				CommunicationDefaults.CONSOLE_HOST), properties.getInt(
				Bench4QProperties.CONSOLE_PORT, CommunicationDefaults.CONSOLE_PORT),
				m_connectionType);
	}
}
