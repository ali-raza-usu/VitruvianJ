package vitruvianJ.serialization.xml;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import vitruvianJ.core.ClassFactory;

public class SimpleXmlDeserializer {
	
	/*
		/// <summary>
        /// Serialize an object into an XmlDocument.
        /// </summary>
        /// <param name="value">The object to serialize.</param>
        /// <returns>The xml document representing the object.</returns>
        public void Deserialize(Document document, Object value)
        {
            Node root = document.getLastChild();
            Deserialize(root, value);
        }

        /// <summary>
        /// Deserialize the node into the given object.
        /// If the given object is null an object will be created.
        /// </summary>
        /// <param name="node">The node defining the object.</param>
        /// <param name="curValue">The current value of the object.</param>
        protected Object Deserialize(Node node, Object value)
		{
        	String innerText = node.getTextContent();
            if ((innerText == null || innerText.equals("")))
                return null;

            Object result = value;

            Class<?> type = value.getClass();
			
			if (type.equals(String.class))
			{
				result = XmlStringEncoder.W3CDecodeString(innerText);
			}
			else if (type.isEnum())
            {
				Enum enumValue = (Enum)value;
				result = Enum.valueOf(enumValue.getClass(), innerText);
            }
			else if (type.equals(boolean.class))
			{
				result = Boolean.parseBoolean(innerText);
			}
			else if (type.equals(byte.class))
			{
                    result = Byte.parseByte(innerText);
			}
            
			else if (type.equals(char.class))
			{
                    result = innerText.charAt(0);
			}
			else if (type.equals(double.class))
			{
                    result = Double.parseDouble(innerText);
			}
			else if (type.equals(float.class))
			{
                    result = Float.parseFloat(innerText);
			}
			else if (type.equals(int.class))
			{
                    result = Integer.parseInt(innerText);
			}
			else if (type.equals(long.class))
			{
                    result = Long.parseLong(innerText);
			}
			else if (type.equals(short.class))
			{
                    result = Short.parseShort(innerText);
			}
			else if (type.equals(Date.class))
            {
                    result = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").parse(innerText);
            }
			else if ((List.class).isAssignableFrom(type))
            {
                DeserializeList(node, (List)result);
            }
            else
            {
                DeserializeObject(node, result);
            }

            return result;
		}

		/// <summary>
		/// Deserialize the object from the node using reflection.
		/// </summary>
		/// <param name="node">The node to desserialize the object from.</param>
		/// <param name="value">The object to deserialize into.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		protected void DeserializeObject(Node node, Object value)
		{
            SimpleXmlTypeSerializationInfo reflectionInfo = SimpleXmlSerializationUtilities.GetReflectionInfo(value.GetType());

            for (int i =0; i<node.getChildNodes().getLength(); i++)
            {
            	Node childNode = node.getChildNodes().item(i);
            	Object pValue = null;
            	Type pType = null;
                if (childNode.getNodeType() == Node.COMMENT_NODE) continue;
                
                SimpleXmlPropertySerializationInfo pInfo = reflectionInfo.GetProperty(childNode.getNodeName());

                if (pInfo != null)
                {
                    if (childNode.getTextContent() != null && !childNode.getTextContent().equals(""))
                    {
                    	pType = pInfo.Info.getGenericReturnType();
                    	if (!pInfo.Info.getGenericReturnType().getClass().isMemberClass())
        					try{
                            pValue = pInfo.Info.invoke(value,null);					
        					}catch(Exception e){ e.printStackTrace();}
        				pValue = Deserialize(childNode, pValue);
        				
        				String methodName = "set"+name;
                    	
        				Class methodClass = null;
        				if(pType.equals(Type.class))
        				{
        					methodClass = (Class) childType;
        				}else
        				{
        					try{
        					methodClass = childValue.getClass();
        					}catch(Exception ex)
        					{
        						System.out.println(childValue + " " + methodName + " " + methodClass);
        					}
        				}
        			
        			    methodClass = FindCompatibleMethodForClassInterface(value.getClass(), methodClass, methodName);			
        				Method method = value.getClass().getMethod(methodName, methodClass);
        				method.invoke(value, childValue);
        				
                    }
                    else
                    {
                        pInfo.Info.SetValue(value, null, null);
                    }
                }
            }
		}

		/// <summary>
		/// Deserialize the list from the node.
		/// </summary>
		/// <param name="node">The node to serialize the list from.</param>
		/// <param name="value">The list to serialize into.</param>
		protected void DeserializeList(Node node, IList value)
		{
            Type elementType = SerializationUtilities.GetElementType(value);

            foreach (Node childNode in node.ChildNodes)
            {
                if (childNode.NodeType != NodeType.Element)
                    continue;

                Object listItem = ClassFactory.CreateObject(elementType);
                listItem = Deserialize(childNode, listItem);

                value.Add(listItem);
            }
		}
		
		*/
}
