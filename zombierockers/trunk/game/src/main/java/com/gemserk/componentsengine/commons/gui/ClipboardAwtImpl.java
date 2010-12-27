package com.gemserk.componentsengine.commons.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * AWT based clipboard impl.
 * @author void
 */
public class ClipboardAwtImpl implements Clipboard, ClipboardOwner {

  /**
   * Put data into the system clipboard.
   * @param data the data
   */
  public void put(final String data) {
    java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(new StringSelection(data), this);
  }

  /**
   * Get string data back from the clipboard.
   * @return string data from clipboard
   */
  public String get() {
    String result = "";
    java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    //odd: the Object param of getContents is not currently used
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText) {
      try {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
      } catch (UnsupportedFlavorException ex) {
        // highly unlikely since we are using a standard DataFlavor
        System.out.println(ex);
        ex.printStackTrace();
      } catch (IOException ex) {
        System.out.println(ex);
        ex.printStackTrace();
      }
    }
    return result;
  }

  /**
   * not used...
   */
  public void lostOwnership(java.awt.datatransfer.Clipboard arg0, Transferable arg1) {
  }
}
