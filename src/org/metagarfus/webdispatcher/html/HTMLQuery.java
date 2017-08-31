package org.metagarfus.webdispatcher.html;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class HTMLQuery
{
  public String path;
  public HashMap<String, String> values;

  public HTMLQuery()
  {
    this.path = "";
    this.values = new HashMap<String, String>();
  }

  public void fromString(String query)
  {
    String parts[];
    parts = query.split("\\?", -1);
    values.clear();
    if (parts.length < 1)
      return;
    path = parts[0];
    if (parts.length < 2)
      return;
    addFormData(parts[1]);
  }

  public void addFormData(String data)
  {
    String parts[], keyPair[], value;
    parts = data.split("&", -1);
    for (String part : parts) {
      keyPair = part.split("=");
      if (keyPair.length < 1)
        continue;
      if (keyPair.length >= 2)
        value = keyPair[1];
      else
        value = "";
      try {
        values.put(keyPair[0], URLDecoder.decode(value, "utf8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }

}
