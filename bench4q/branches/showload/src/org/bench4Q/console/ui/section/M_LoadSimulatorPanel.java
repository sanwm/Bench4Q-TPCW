/**
 * =========================================================================
 * 					Bench4Q version 1.0.0
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
package org.bench4Q.console.ui.section;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bench4Q.console.common.Resources;
import org.bench4Q.console.model.ConfigModel;

public class M_LoadSimulatorPanel extends JPanel {

	private final Resources m_resources;

	private JLabel typeLabel;
	private JLabel URLLabel;
	private JLabel mixLabel;

	private JLabel warmupLabel;
	private JLabel cooldownLabel;
	private JLabel intervalLabel;

	private JLabel typeExplain1;
	private JLabel typeExplain2;
	private JLabel URLExplain;
	private JLabel mixExplain;

	private JLabel warmupExplain;
	private JLabel cooldownExplain;

	private JComboBox type;
	private JTextField URL;
	private JComboBox mix;

	private JTextField warmup;
	private JTextField cooldown;
	private ConfigModel m_configModel;
	private JTextField interval;

	public M_LoadSimulatorPanel(Resources resources, ConfigModel fileLoader) {

		m_resources = resources;
		this.m_configModel = fileLoader;

		this.setLayout(new GridBagLayout());

		typeLabel = new JLabel(m_resources.getString("GenelPanel.typeLabel"), SwingConstants.RIGHT);
		intervalLabel = new JLabel(m_resources.getString("GenelPanel.intervalLabel"),
				SwingConstants.RIGHT);
		URLLabel = new JLabel(m_resources.getString("GenelPanel.URLLabel"), SwingConstants.RIGHT);
		mixLabel = new JLabel(m_resources.getString("GenelPanel.mixLabel"), SwingConstants.RIGHT);

		warmupLabel = new JLabel(m_resources.getString("GenelPanel.warmupLabel"),
				SwingConstants.RIGHT);
		cooldownLabel = new JLabel(m_resources.getString("GenelPanel.cooldownLabel"),
				SwingConstants.RIGHT);

		typeExplain1 = new JLabel(m_resources.getString("GenelPanel.typeExplain1"),
				SwingConstants.RIGHT);
		// typeExplain1.setFont();
		typeExplain1.setBackground(Color.gray);
		typeExplain2 = new JLabel(m_resources.getString("GenelPanel.typeExplain2"),
				SwingConstants.RIGHT);
		URLExplain = new JLabel(m_resources.getString("GenelPanel.URLExplain"),
				SwingConstants.RIGHT);
		mixExplain = new JLabel(m_resources.getString("GenelPanel.mixExplain"),
				SwingConstants.RIGHT);

		warmupExplain = new JLabel(m_resources.getString("GenelPanel.warmupExplain"),
				SwingConstants.RIGHT);
		cooldownExplain = new JLabel(m_resources.getString("GenelPanel.cooldownExplain"),
				SwingConstants.RIGHT);

		DocumentListener doclistener = new DocListener();
		ActionListener actionlistener = new Actionlistener();

		type = new JComboBox();
		mix = new JComboBox();
		type.addItem(m_resources.getString("GenelPanel.ebopen"));
		type.addItem(m_resources.getString("GenelPanel.fullopen"));
		type.addItem(m_resources.getString("GenelPanel.closed"));
		type.addActionListener(actionlistener);
		if (fileLoader.getArgs().getRbetype().equalsIgnoreCase("EBOpen")) {
			type.setSelectedIndex(0);
		} else if (fileLoader.getArgs().getRbetype().equalsIgnoreCase("FullOpen")) {
			type.setSelectedIndex(1);
		} else if (fileLoader.getArgs().getRbetype().equalsIgnoreCase("closed")) {
			type.setSelectedIndex(2);
		} else {
			// error handle
		}
		mix.addItem(m_resources.getString("GenelPanel.browsing"));
		mix.addItem(m_resources.getString("GenelPanel.shopping"));
		mix.addItem(m_resources.getString("GenelPanel.ordering"));
		mix.addActionListener(actionlistener);
		if (fileLoader.getArgs().getMix().equalsIgnoreCase("browsing")) {
			mix.setSelectedIndex(0);
		} else if (fileLoader.getArgs().getMix().equalsIgnoreCase("shopping")) {
			mix.setSelectedIndex(1);
		} else if (fileLoader.getArgs().getMix().equalsIgnoreCase("ordering")) {
			mix.setSelectedIndex(2);
		} else {
			// error handle
		}

		interval = new JTextField(String.valueOf(fileLoader.getArgs().getInterval()));
		interval.getDocument().addDocumentListener(doclistener);

		URL = new JTextField(fileLoader.getArgs().getBaseURL());
		URL.getDocument().addDocumentListener(doclistener);

		warmup = new JTextField(String.valueOf(fileLoader.getArgs().getPrepair()));
		warmup.getDocument().addDocumentListener(doclistener);

		cooldown = new JTextField(String.valueOf(fileLoader.getArgs().getCooldown()));
		cooldown.getDocument().addDocumentListener(doclistener);

		if ((type.getSelectedIndex() == 2) && (interval != null)) {
			interval.setVisible(false);
			intervalLabel.setVisible(false);
		}

		this.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));
		this.add(type, new GridBagConstraints(1, 0, 1, 1, 100.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		this.add(intervalLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));
		this.add(interval, new GridBagConstraints(3, 0, 1, 1, 50.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 1));
		this.add(new JLabel(" "), new GridBagConstraints(4, 0, 1, 1, 50.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));

		this.add(typeExplain1, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 1, 1));
		this.add(typeExplain2, new GridBagConstraints(0, 2, 5, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 1, 1));

		this.add(mixLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
		this.add(mix, new GridBagConstraints(1, 3, 4, 1, 100.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 1));
		this.add(mixExplain, new GridBagConstraints(0, 4, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		this.add(URLLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
		this.add(URL, new GridBagConstraints(1, 5, 4, 1, 100.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 1));
		this.add(URLExplain, new GridBagConstraints(0, 6, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		this.add(warmupLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
		this.add(warmup, new GridBagConstraints(1, 7, 4, 1, 100.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 1));
		this.add(warmupExplain, new GridBagConstraints(0, 8, 5, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		this.add(cooldownLabel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 1, 1));
		this.add(cooldown, new GridBagConstraints(1, 9, 4, 1, 100.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 1));
		this.add(cooldownExplain, new GridBagConstraints(0, 10, 5, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		this.add(new JLabel(), new GridBagConstraints(0, 11, 5, 1, 100.0, 100.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 1, 1));

		m_configModel.addListener(new ConfigModel.AbstractListener() {
			public void isArgsChanged() {
				resetConfig();
			}
		});

	}

	protected void resetConfig() {

		if (m_configModel.getArgs().getRbetype().equalsIgnoreCase("EBOpen")) {
			type.setSelectedIndex(0);
			if (interval != null) {
				interval.setText(String.valueOf(m_configModel.getArgs().getInterval()));
				interval.setVisible(true);
				intervalLabel.setVisible(true);
			}

		} else if (m_configModel.getArgs().getRbetype().equalsIgnoreCase("FullOpen")) {
			type.setSelectedIndex(1);
			if (interval != null) {
				interval.setText(String.valueOf(m_configModel.getArgs().getInterval()));
				interval.setVisible(true);
				intervalLabel.setVisible(true);
			}
		} else if (m_configModel.getArgs().getRbetype().equalsIgnoreCase("closed")) {
			type.setSelectedIndex(2);
			if (interval != null) {
				interval.setText(String.valueOf(m_configModel.getArgs().getInterval()));
				interval.setVisible(false);
				intervalLabel.setVisible(false);
			}
		}

		if (m_configModel.getArgs().getMix().equalsIgnoreCase("browsing")) {
			mix.setSelectedIndex(0);

		} else if (m_configModel.getArgs().getMix().equalsIgnoreCase("shopping")) {
			mix.setSelectedIndex(1);
		} else if (m_configModel.getArgs().getMix().equalsIgnoreCase("ordering")) {
			mix.setSelectedIndex(2);
		}
		URL.setText(String.valueOf(m_configModel.getArgs().getBaseURL()));
		warmup.setText(String.valueOf(m_configModel.getArgs().getPrepair()));
		cooldown.setText(String.valueOf(m_configModel.getArgs().getCooldown()));

	}

	public void CreateExplain(JLabel label, String context) {
		label = new JLabel(m_resources.getString(context), SwingConstants.RIGHT);
		// typeExplain1.setFont();
		typeExplain1.setBackground(Color.gray);
	}

	public void setConfigue() {
		double intervalValue = 1;

		int warmupValue = 0;
		int cooldownValue = 0;
		String URLValue = null;
		try {
			if ((interval.getText().trim() != null) && (!interval.getText().trim().equals(""))) {
				intervalValue = Double.parseDouble(interval.getText().trim());
			}

			if ((warmup.getText().trim() != null) && (!warmup.getText().equals(""))) {
				warmupValue = Integer.parseInt(warmup.getText().trim());
			}
			if ((cooldown.getText().trim() != null) && (!cooldown.getText().equals(""))) {
				cooldownValue = Integer.parseInt(cooldown.getText().trim());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		URLValue = URL.getText().trim();

		m_configModel.getArgs().setInterval(intervalValue);

		m_configModel.getArgs().setPrepair(warmupValue);
		m_configModel.getArgs().setCooldown(cooldownValue);
		m_configModel.getArgs().setBaseURL(URLValue);

	}

	private class DocListener implements DocumentListener {
		public void insertUpdate(DocumentEvent event) {
			setConfigue();
		}

		public void removeUpdate(DocumentEvent event) {
			setConfigue();
		}

		public void changedUpdate(DocumentEvent event) {
			setConfigue();
		}
	}

	private class Actionlistener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (type.getSelectedIndex() == 0) {
				m_configModel.getArgs().setRbetype("EBOpen");
				if (interval != null) {
					interval.setVisible(true);
					intervalLabel.setVisible(true);
				}

			} else if (type.getSelectedIndex() == 1) {
				m_configModel.getArgs().setRbetype("FullOpen");
				if (interval != null) {
					interval.setVisible(true);
					intervalLabel.setVisible(true);
				}
			} else if (type.getSelectedIndex() == 2) {
				m_configModel.getArgs().setRbetype("closed");
				if (interval != null) {
					interval.setVisible(false);
					intervalLabel.setVisible(false);
				}
			} else {

			}

			if (mix.getSelectedIndex() == 0) {
				m_configModel.getArgs().setMix("browsing");
			} else if (mix.getSelectedIndex() == 1) {
				m_configModel.getArgs().setMix("shopping");
			} else if (mix.getSelectedIndex() == 2) {
				m_configModel.getArgs().setMix("ordering");
			} else {

			}

		}

	}

}
