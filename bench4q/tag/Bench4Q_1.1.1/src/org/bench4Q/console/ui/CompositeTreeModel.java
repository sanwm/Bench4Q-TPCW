/**
 * =========================================================================
 * 					Bench4Q version 1.1.1
 * =========================================================================
 * 
 * Bench4Q is available on the Internet at http://forge.ow2.org/projects/jaspte
 * You can find latest version there. 
 * 
 * Distributed according to the GNU Lesser General Public Licence. 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by   
 * the Free Software Foundation; either version 2.1 of the License, or any
 * later version.
 * 
 * SEE Copyright.txt FOR FULL COPYRIGHT INFORMATION.
 * 
 * This source code is distributed "as is" in the hope that it will be
 * useful.  It comes with no warranty, and no author or distributor
 * accepts any responsibility for the consequences of its use.
 *
 *
 * This version is a based on the implementation of TPC-W from University of Wisconsin. 
 * This version used some source code of The Grinder.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 *  * Initial developer(s): Zhiquan Duan.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * 
 */

package org.bench4Q.console.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * {@link TreeModel} that combines other tree models.
 * 
 * <p>
 * The composed {@link TreeModel} implementations must be aware of which nodes
 * belong to them, and return null answers for nodes that don't belong to them.
 * </p>
 */
final class CompositeTreeModel implements TreeModel {

	private final List m_wrappers = new ArrayList();
	private final EventListenerList m_listeners = new EventListenerList();

	private final Object m_rootNode = new Object();

	CompositeTreeModel() {
	}

	void addTreeModel(TreeModel treeModel, boolean includeRoot) {

		final DelegateWrapper wrapper;

		if (includeRoot) {
			wrapper = new RootWrapper(treeModel);
		} else {
			wrapper = new FirstLevelWrapper(treeModel);
		}

		final EventListener[] eventListeners = m_listeners.getListeners(TreeModelListener.class);

		for (int i = 0; i < eventListeners.length; ++i) {
			wrapper.addTreeModelListener((TreeModelListener) eventListeners[i]);
		}

		m_wrappers.add(wrapper);
	}

	public Object getRoot() {
		return m_rootNode;
	}

	public Object getChild(Object parent, int index) {
		if (index < 0) {
			return null;
		} else if (parent.equals(getRoot())) {
			int base = 0;

			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

				final int numberOfTopLevelNodes = wrapper.getNumberOfTopLevelNodes();

				if (index - base < numberOfTopLevelNodes) {
					return wrapper.getTopLevelNode(index - base);
				}

				base += numberOfTopLevelNodes;
			}
		} else {
			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

				final Object delegateAnswer = wrapper.getChild(parent, index);

				if (delegateAnswer != null) {
					return delegateAnswer;
				}
			}
		}

		return null;
	}

	public int getChildCount(Object parent) {
		if (parent.equals(getRoot())) {
			int answer = 0;

			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();
				answer += wrapper.getNumberOfTopLevelNodes();
			}

			return answer;
		} else {
			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

				final int delegateAnswer = wrapper.getChildCount(parent);

				if (delegateAnswer != 0) {
					return delegateAnswer;
				}
			}
		}

		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {

		if (parent == null || child == null) {
			// The TreeModel Javadoc says we should do this.
			return -1;
		}

		if (parent.equals(getRoot())) {
			int base = 0;

			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

				final int delegateAnswer = wrapper.getIndexOfTopLevelNode(child);

				if (delegateAnswer != -1) {
					return base + delegateAnswer;
				}

				base += wrapper.getNumberOfTopLevelNodes();
			}
		} else {
			final Iterator iterator = m_wrappers.iterator();

			while (iterator.hasNext()) {
				final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

				final int delegateAnswer = wrapper.getIndexOfChild(parent, child);

				if (delegateAnswer != -1) {
					return delegateAnswer;
				}
			}
		}

		return -1;
	}

	public boolean isLeaf(Object node) {
		final Iterator iterator = m_wrappers.iterator();

		while (iterator.hasNext()) {
			final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();

			if (wrapper.isLeaf(node)) {
				return true;
			}
		}

		return false;
	}

	public void addTreeModelListener(TreeModelListener listener) {
		m_listeners.add(TreeModelListener.class, listener);

		final Iterator iterator = m_wrappers.iterator();

		while (iterator.hasNext()) {
			final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();
			wrapper.addTreeModelListener(listener);
		}
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		m_listeners.remove(TreeModelListener.class, listener);

		final Iterator iterator = m_wrappers.iterator();

		while (iterator.hasNext()) {
			final DelegateWrapper wrapper = (DelegateWrapper) iterator.next();
			wrapper.removeTreeModelListener(listener);
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// Do nothing.
	}

	private abstract static class DelegateWrapper {
		private final TreeModel m_model;
		private final Map m_delegateListenerMap = new HashMap();

		protected DelegateWrapper(TreeModel model) {
			m_model = model;
		}

		public abstract Object getTopLevelNode(int i);

		public abstract int getNumberOfTopLevelNodes();

		public abstract int getIndexOfTopLevelNode(Object node);

		public final TreeModel getModel() {
			return m_model;
		}

		public final Object getChild(Object parent, int index) {
			try {
				return getModel().getChild(parent, index);
			} catch (ClassCastException e) {
				return null;
			}
		}

		public int getChildCount(Object parent) {
			try {
				return getModel().getChildCount(parent);
			} catch (ClassCastException e) {
				return 0;
			}
		}

		public int getIndexOfChild(Object parent, Object child) {
			try {
				return getModel().getIndexOfChild(parent, child);
			} catch (ClassCastException e) {
				return -1;
			}
		}

		public final boolean isLeaf(Object node) {
			try {
				return getModel().isLeaf(node);
			} catch (ClassCastException e) {
				return false;
			}
		}

		protected abstract TreeModelEvent mapTreeModelEvent(TreeModelEvent e);

		public void addTreeModelListener(final TreeModelListener listener) {

			final TreeModelListener delegateListener = new TreeModelListener() {
				public void treeNodesChanged(TreeModelEvent e) {
					listener.treeNodesChanged(mapTreeModelEvent(e));
				}

				public void treeNodesInserted(TreeModelEvent e) {
					listener.treeNodesInserted(mapTreeModelEvent(e));
				}

				public void treeNodesRemoved(TreeModelEvent e) {
					listener.treeNodesRemoved(mapTreeModelEvent(e));
				}

				public void treeStructureChanged(TreeModelEvent e) {
					listener.treeStructureChanged(mapTreeModelEvent(e));
				}
			};

			m_delegateListenerMap.put(listener, delegateListener);
			getModel().addTreeModelListener(delegateListener);
		}

		public void removeTreeModelListener(TreeModelListener listener) {
			final TreeModelListener delegateListener = (TreeModelListener) m_delegateListenerMap
					.remove(listener);

			if (delegateListener != null) {
				getModel().removeTreeModelListener(delegateListener);
			}
		}
	}

	private final class RootWrapper extends DelegateWrapper {
		public RootWrapper(TreeModel model) {
			super(model);
		}

		public Object getTopLevelNode(int i) {
			return i == 0 ? getModel().getRoot() : null;
		}

		public int getNumberOfTopLevelNodes() {
			return 1;
		}

		public int getIndexOfTopLevelNode(Object node) {
			return node.equals(getModel().getRoot()) ? 0 : -1;
		}

		protected TreeModelEvent mapTreeModelEvent(TreeModelEvent e) {

			final Object[] path = e.getPath();

			if (path.length > 0 && path[0].equals(getRoot())) {
				return e;
			}

			final Object[] newPath = new Object[path.length + 1];
			System.arraycopy(path, 0, newPath, 1, path.length);
			newPath[0] = getRoot();

			return new TreeModelEvent(this, newPath, e.getChildIndices(), e.getChildren());
		}
	}

	private final class FirstLevelWrapper extends DelegateWrapper {
		public FirstLevelWrapper(TreeModel model) {
			super(model);
		}

		public Object getTopLevelNode(int i) {
			return super.getChild(getModel().getRoot(), i);
		}

		public int getNumberOfTopLevelNodes() {
			return super.getChildCount(getModel().getRoot());
		}

		public int getIndexOfTopLevelNode(Object node) {
			return super.getIndexOfChild(getModel().getRoot(), node);
		}

		protected TreeModelEvent mapTreeModelEvent(TreeModelEvent e) {

			final Object[] path = e.getPath();

			if (path.length > 0) {
				path[0] = getRoot();
			}

			return new TreeModelEvent(this, path, e.getChildIndices(), e.getChildren());
		}
	}
}
