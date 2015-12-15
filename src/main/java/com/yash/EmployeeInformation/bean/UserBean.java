package com.yash.EmployeeInformation.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.yash.EmployeeInformation.service.ManagerServiceLocal;
import com.yash.EmployeeInformation.service.UserServiceLocal;

@ManagedBean
@SessionScoped
public class UserBean {

	@EJB
	UserServiceLocal userService;

	private Boolean check;
	private String name;
	private String password;

	private String message;

	private Part file;

	FacesContext facesContext = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String authenticate() {
		check = getConnection(name, password);
		if (check == false) {
			return "index.xhtml?faces-redirect=true&error=Invalid UserName And Password";
		} else {
			session.setAttribute("eusername", name);
		}
		return "welcome.xhtml";
	}

	public boolean getConnection(String ldapUsername, String ldapPassword) {

		final String ldapAdServer = "ldap://inidradc01.yash.com/";

		Hashtable<String, Object> env = new Hashtable<String, Object>();

		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		if (ldapUsername != null) {
			env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
		}
		if (ldapPassword != null) {
			env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
		}

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapAdServer);

		env.put("java.naming.ldap.attributes.binary", "objectSID");
		env.put(Context.REFERRAL, "follow");

		try {
			@SuppressWarnings("unused")
			DirContext ctx = new InitialDirContext(env);

			return true;
		} catch (Exception e) {

			return false;

		}
	}

	public String uploadFile() {
		Part uploadedFile = getFile();

		if (uploadedFile == null) {
			return "welcome.xhtml?uploadmessage=please select a file to upload&faces-redirect=true";
		}
		String filename = getSubmittedFileName(uploadedFile);
		int lastindex = filename.lastIndexOf(".");
		String filetype = filename.substring(lastindex + 1, lastindex + 4);
		if (filetype.equals("doc")) {
			String uploadmessage = helpUpload();
			return "welcome.xhtml?uploadmessage=" + uploadmessage + "&faces-redirect=true";
		} else {
			return "welcome.xhtml?uploadmessage=please upload a valid word document&faces-redirect=true";
		}
	}

	public String helpUpload() {
		Part uploadedFile = getFile();
		/*
		 * String realpath = (String)
		 * FacesContext.getCurrentInstance().getExternalContext().getRealPath(
		 * "/"); int index = realpath.lastIndexOf("\\"); String path =
		 * realpath.substring(0, index + 1);
		 */
		File dir = new File("\\\\YITRNG06DT\\uploaded");
		dir.mkdirs();
		String filename = "sample";
		if (null != session.getAttribute("eusername"))
			filename = (String) session.getAttribute("eusername");
		final Path destination = Paths.get(dir.getAbsolutePath() + "/" + filename + ".doc");
		InputStream bytes = null;
		if (null != uploadedFile) {
			try {
				bytes = uploadedFile.getInputStream();
				if (!destination.toFile().exists())
					Files.copy(bytes, destination);
				else {
					destination.toFile().delete();
					Files.copy(bytes, destination);
				}
				return "upload succesfull";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error occured please try again after sometime";
			} //
		}
		return "upload Fail";
	}

	public static String getSubmittedFileName(Part filePart) {
		String header = filePart.getHeader("content-disposition");
		if (header == null)
			return null;
		for (String headerPart : header.split(";")) {
			if (headerPart.trim().startsWith("filename")) {
				return headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

}
