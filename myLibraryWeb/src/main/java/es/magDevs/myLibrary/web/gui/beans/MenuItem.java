package es.magDevs.myLibrary.web.gui.beans;

/**
 * Bean para todos los datos de una entrada del menu (texto, imagen, link, ...)
 * 
 * @author javier.vaquero
 * 
 */
public class MenuItem {
	private String text;
	private String img;
	private String link;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
