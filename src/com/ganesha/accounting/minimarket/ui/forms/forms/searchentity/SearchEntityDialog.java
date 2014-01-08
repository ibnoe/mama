package com.ganesha.accounting.minimarket.ui.forms.forms.searchentity;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.ui.commons.MyTableModel;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtil;
import com.ganesha.model.TableEntity;

public class SearchEntityDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJTable table;

	private Class<?> entityClass;
	private String selectedCode;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private XJButton btnPilih;

	public SearchEntityDialog(Window parent, Class<?> entityClass) {
		super(parent);
		this.entityClass = entityClass;

		setTitle("Cari Entitas");
		getContentPane().setLayout(
				new MigLayout("", "[300, grow]", "[][300,grow][]"));

		table = new XJTable();
		initTable();

		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow]", "[][]"));

		XJLabel lblKode = new XJLabel();
		lblKode.setText("Kode");
		pnlFilter.add(lblKode, "cell 0 0,alignx trailing");

		txtKode = new XJTextField();
		txtKode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlFilter.add(txtKode, "cell 1 0,growx");
		txtKode.setColumns(10);

		XJLabel lblName = new XJLabel();
		lblName.setText("Name");
		pnlFilter.add(lblName, "cell 0 1,alignx trailing");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1,growx");
		txtNama.setColumns(10);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[]", "[]"));

		btnPilih = new XJButton();
		btnPilih.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					pilih();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		panel.add(btnPilih, "cell 0 0");
		btnPilih.setText("<html><center>Pilih<br/>[F1]</center><html>");

		pack();
		setLocationRelativeTo(null);

		try {
			loadData();
		} catch (AppException ex) {
			ExceptionHandler.handleException(ex);
		}
	}

	public String getSelectedCode() {
		return selectedCode;
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F1:
			btnPilih.doClick();
			break;
		default:
			break;
		}
	}

	private void initTable() {
		MyTableModel tableModel = new MyTableModel();
		tableModel.setColumnIdentifiers(new String[] { "Kode", "Name" });
		tableModel.setColumnEditable(new boolean[] { false, false });
		table.setModel(tableModel);
	}

	private void loadData() throws AppException {
		Session session = HibernateUtil.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();

			Criteria criteria = session.createCriteria(entityClass);
			criteria.add(Restrictions.like("code", "%" + code + "%")
					.ignoreCase());
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
			criteria.add(Restrictions.eq("disabled", false));
			criteria.add(Restrictions.eq("deleted", false));

			List<?> searchResults = criteria.list();

			MyTableModel tableModel = (MyTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				TableEntity searchResult = (TableEntity) searchResults.get(i);

				Class<?> clazz = searchResult.getClass();
				Method getCodeMethod = clazz.getDeclaredMethod("getCode");
				Method getNameMethod = clazz.getDeclaredMethod("getName");

				String codeValue = (String) getCodeMethod.invoke(searchResult);
				String nameValue = (String) getNameMethod.invoke(searchResult);

				tableModel.setValueAt(codeValue, i, 0);
				tableModel.setValueAt(nameValue, i, 1);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AppException(e);
		} catch (IllegalAccessException e) {
			throw new AppException(e);
		} catch (IllegalArgumentException e) {
			throw new AppException(e);
		} catch (InvocationTargetException e) {
			throw new AppException(e);
		} finally {
			session.close();
		}
	}

	private void pilih() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}
		selectedCode = (String) table.getModel().getValueAt(selectedRow, 0);
		dispose();
	}
}
