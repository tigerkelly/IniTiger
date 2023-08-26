package application;

import java.io.File;

import com.rkw.IniFile;

import javafx.scene.control.Accordion;

public class FileInfo {

	private File fd;
	private IniFile ini;
	private Accordion acc;
	
	public FileInfo(File fd) {
		this.fd = fd;
		
		if (fd.exists() == true)
			ini = new IniFile(fd.getAbsolutePath());
	}

	public File getFd() {
		return fd;
	}

	public void setFd(File fd) {
		this.fd = fd;
	}

	public IniFile getIni() {
		return ini;
	}

	public void setIni(IniFile ini) {
		this.ini = ini;
	}

	public Accordion getAcc() {
		return acc;
	}

	public void setAcc(Accordion acc) {
		this.acc = acc;
	}
}
