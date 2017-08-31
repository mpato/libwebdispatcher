package org.metagarfus.webdispatcher.html;

public class Mime
{
  public static String getType(String path)
  {
    String parts[], ext;
    parts = path.split("\\.");
    if (parts.length < 2)
      return "text/plain";
    ext = parts[parts.length - 1].toLowerCase().trim();
    if (ext.equals("js"))
     return "application/javascript";
    if (ext.equals("apk"))
      return "application/vnd.android.package-archive";
    return "text/plain";
  }
}
