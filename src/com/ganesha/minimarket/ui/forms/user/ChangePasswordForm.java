package com.ganesha.minimarket.ui.forms.user;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.UserFacade;

public class ChangePasswordForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPasswordField txtOldPassword;
	private XJPasswordField txtNewPassword;
	private XJPasswordField txtConfirmNewPassword;
	private XJButton btnBatal;

	public ChangePasswordForm(Window parent) {
		super(parent);
		setTitle("Form User");

		getContentPane().setLayout(new MigLayout("", "[]", "[grow][]"));

		JPanel pnlInput = new JPanel();
		getContentPane().add(pnlInput, "cell 0 0,grow");
		pnlInput.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlInput.setLayout(new MigLayout("", "[][200]", "[][][]"));

		XJLabel lblOldPassword = new XJLabel();
		pnlInput.add(lblOldPassword, "cell 0 0");
		lblOldPassword.setText("Password Lama");

		txtOldPassword = new XJPasswordField();
		pnlInput.add(txtOldPassword, "cell 1 0,growx");

		XJLabel lblNewPassword = new XJLabel();
		pnlInput.add(lblNewPassword, "cell 0 1");
		lblNewPassword.setText("Password Baru");

		txtNewPassword = new XJPasswordField();
		pnlInput.add(txtNewPassword, "cell 1 1,growx");

		XJLabel lblConfirmNewPassword = new XJLabel();
		lblConfirmNewPassword.setText("Konfirmasi Password Baru");
		pnlInput.add(lblConfirmNewPassword, "cell 0 2");

		txtConfirmNewPassword = new XJPasswordField();
		pnlInput.add(txtConfirmNewPassword, "cell 1 2,growx");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ChangePasswordForm.this,
							ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 2 0");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void save() throws ActionTypeNotSupported, UserException,
			AppException {

		String oldPassword = new String(txtOldPassword.getPassword()).trim();
		String newPassword = new String(txtNewPassword.getPassword()).trim();
		String confirmNewPassword = new String(
				txtConfirmNewPassword.getPassword()).trim();

		validateForm(oldPassword, newPassword, confirmNewPassword);

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			UserFacade facade = UserFacade.getInstance();
			facade.changePassword(Main.getUserLogin().getId(), new String(
					txtOldPassword.getPassword()),
					new String(txtNewPassword.getPassword()), session);
			session.getTransaction().commit();
			dispose();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm(String oldPassword, String newPassword,
			String confirmNewPassword) throws UserException {
		if (oldPassword.equals("")) {
			throw new UserException("Password Lama harus diisi");
		}

		if (newPassword.equals("")) {
			throw new UserException("Password Baru harus diisi");
		}

		if (confirmNewPassword.equals("")) {
			throw new UserException("Konfirmasi Password Baru harus diisi");
		}

		if (!newPassword.equals(confirmNewPassword)) {
			throw new UserException("Konfirmasi Password Baru tidak sesuai");
		}
	}
}