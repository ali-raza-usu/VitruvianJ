package vitruvianJ.serialization.xml;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
public class XmlStringEncoder {

	public static String W3CEncodeString(String value)
	{
		StringBuilder sb = new StringBuilder();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			switch (chars[i])
			{
				case '&':
					sb.append("&amp;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '\"':
					sb.append("&quot;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				default:
					sb.append(chars[i]);
					break;
			}
		}

		return sb.toString();
	}

	/// <summary>
	/// Decode the String using W3C standards.
	/// </summary>
	/// <param name="value">The String to decode.</param>
	/// <returns>The decoded String.</returns>
	public static String W3CDecodeString(String value)
	{
		StringBuilder sb = new StringBuilder();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			switch (chars[i])
			{
				case '&':
					{
						String token = "";
						for (int j = i; j < chars.length; j++, i++)
						{
							token += chars[j];
							if (chars[j] == ';')
								break;
						}
						//switch (token)
						//{
						if(token.equals("&amp;"))
							token = "&";
						//	break;
						else if(token.equals("&apos;"))
							token = "\'";
							//break;
						else if(token.equals("&quot;"))
							token = "\"";
							//break;
						else if(token.equals("&lt;"))
							token = "<";
							//break;
						else if(token.equals("&gt;"))
							token = ">";
								//break;
						//}
						sb.append(token);
						break;
					}
				default:
					sb.append(chars[i]);
					break;
			}
		}

		return sb.toString();
	}

	/// <summary>
	/// Determine if the given value is a valid xml document.
	/// </summary>
	/// <param name="value"></param>
	/// <returns>Returns null on invalid text.</returns>
	public static Document ToXmlDocument(String value)
	{
		if ((value == null) || (value.length() <= 0))
			return null;

		if (value.charAt(0) == '<')
		{
			try
			{
				DocumentBuilder doc = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				return doc.parse(value);
				//doc.gLoadXml(value);
				//return doc;
			}
			catch (Exception e)
			{
				return null;
				// this is an expected exception in the case of invalid text but most likely in the case of multiple root nodes (also an error)
			}
		}
		else
			return null;
	}
}
