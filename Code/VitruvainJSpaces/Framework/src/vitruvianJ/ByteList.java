package vitruvianJ;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import java.util.LinkedList;
import java.util.List;
import java.nio.ByteBuffer;



public class ByteList
	{
		private final int SECTION_SIZE = 1024;

		private List<byte[]> _sections = new LinkedList<byte[]>();
		private int _curSectionIndex = 0;
		private int _curIndex = 0;
		private int _offset = 0;
		private int _index = 0;

		public ByteList()
		{
			_sections.add(new byte[SECTION_SIZE]);
		}

		public ByteList(Object... items) throws Exception
		{
			_sections.add(new byte[SECTION_SIZE]);

			
			for(int i =0; i<items.length; i++)
			{
				Object item = items[i];
				Type type = item.getClass();

				if (type.equals(Boolean.class))
					throw new Exception();
				else if (type.equals(byte.class))
					Add((Byte)item);				
				else if (type.equals(char.class))
					Add((Character)item); // UInt16				
				else if (type.equals(double.class))
					Add((Double)item);
				else if (type.equals(float.class))
					Add((Float)item);
				else if (type.equals(int.class))
					Add((Integer)item);				
				else if (type.equals(long.class))
					Add((Long)item);				
				else if (type.equals(short.class))
					Add((Short)item);				
				else if (type.equals(String.class))
					Add((String)item);
				else if (item.equals(byte[].class))
					Add((byte[])item);
				else
					throw new Exception();
			}
		}
		
		public byte getThis(int index) throws Exception
		{
			if (index < 0)					
				throw new Exception();
				byte[] section = GetSection(index);
				return section[index];
		}
	
		public void setThis(int index) throws Exception
		{
				Grow(index);
				byte[] section = GetSection(index);
				section[index] = getThis(index);			
		}

		public List<byte[]> getSections()
		{
			return _sections;
		}
		public void Clear()
		{
			_sections.clear();
			_curSectionIndex = 0;
			_curIndex = 0;
		}

		public int getLength()
		{			
				return _curSectionIndex*SECTION_SIZE + _curIndex;			
		}

		private void Grow(int maxIndex)
		{
			int sectionIndex = maxIndex / SECTION_SIZE;
			for (int i = _sections.size()-1; i < sectionIndex; i++)
				_sections.add(new byte[SECTION_SIZE]);
		}

		public byte[] GetSection(int index) throws Exception
		{
			_index = index;
			int sectionIndex = index / SECTION_SIZE;

			if (sectionIndex >= _sections.size())
				throw new Exception();
				//throw new ApplicationException();

			index -= sectionIndex * SECTION_SIZE;				
			return _sections.get(sectionIndex);
		}

		public void Add(ByteList value)
		{
            for (int i = 0; i <= value._curSectionIndex; i++)
            {
                if (i < value._curSectionIndex)
                    Add(value._sections.get(i), 0, SECTION_SIZE);
                else
                    Add(value._sections.get(i), 0, value._curIndex);
            }
		}

        public void Add(byte value)
		{
			Add(new byte[] {value});
		}
		
		public void Add(Boolean value)
		{
			if (value)
				Add(new byte[] {1});
			else
				Add(new byte[] {0});
		}
		
		public void Add(char value)
		{			
			
			Add(new byte[]{(byte)(value & 0xff), (byte)(value >> 8 & 0xff)});
		}

		public void Add(int value)
		{
			Add(new byte[] {
					   (byte)(value >>> 24),
					   (byte)(value >>> 16),
					   (byte)(value >>> 8),
					   (byte)value});
		}

		

		public void Add(long value)
		{
			byte[] arr=new byte[8];
			for(int i=0;i<arr.length;i++){
			arr[i]=(byte)(value&0xff);
			value=value>>>8;
			}
			Add(arr);
		}

		
		public void Add(double value) throws Exception
		{			
			 ByteArrayOutputStream byte_out = new ByteArrayOutputStream ();
			 DataOutputStream data = new DataOutputStream(byte_out);
			 data.writeDouble(value);
			 data.flush();
			 Add(byte_out.toByteArray());
			 byte_out.close();
			 
			 
		}

		public void Add(float value)
		{
			 ByteBuffer buf = ByteBuffer.allocate(4);
		     buf.putFloat(value);		      
			Add(buf.array());
		}

		public void Add(String value, Boolean nullTerminate)
		{
			Add(value.getBytes());
			if (nullTerminate)
				Add((byte)0);
		}

		public void Add(String value)
		{
			int size = value.length();
			Add((int)size);
			Add(value, false);
		}

		public void Add(byte[] value)
		{
			Grow(_curIndex + value.length);

			int cnt = 0;
			while (cnt < value.length)
			{
				byte[] section = _sections.get(_curSectionIndex);
				
				int blockSize = (int)Math.min(SECTION_SIZE - _curIndex, value.length - cnt);
				BlockCopy(getArrayObject(value), cnt, getArrayObject(section), _curIndex, blockSize);
				
				cnt += blockSize;
				_curIndex += blockSize;
				if (_curIndex == SECTION_SIZE)
				{
					_curIndex = 0;
					_curSectionIndex++;

                    if (_curSectionIndex >= _sections.size())
                        _sections.add(new byte[SECTION_SIZE]);
                }
			}
		}

		public void BlockCopy(Array  _src, int _srcOffset,Array _dst,int _dstOffset,int _count)
		{			
			int j = _dstOffset;
			try{
				for(int i=_srcOffset; i<_srcOffset+_count; i++)
				{
					Array.set(_dst,j++, Array.get(_src, i));
				}
			}
			catch(Exception e)
			{
				
			}
		}

		public void Add(byte[] value, int offset, int length)
		{
			
			Grow(_curIndex + length);

			int cnt = 0;
			while (cnt < length)
			{
				byte[] section = _sections.get(_curSectionIndex);

				int blockSize = (int)Math.min(SECTION_SIZE - _curIndex, length - cnt);
				BlockCopy(getArrayObject(value), offset + cnt, getArrayObject(section), _curIndex, blockSize);

				cnt += blockSize;
				_curIndex += blockSize;
				if (_curIndex == SECTION_SIZE)
				{
					_curIndex = 0;
					_curSectionIndex++;

                    if (_curSectionIndex >= _sections.size())
                        _sections.add(new byte[SECTION_SIZE]);
				}
			}
		}
		
		
		
		public ByteList GetByteList(int offset, int length)
		{
			ByteList result = new ByteList();
			result.FromBytes(GetBytes(offset, length));
			return result;
		}
		
		
		public byte GetByte(int offset)
		{
			return GetBytes(offset, 1)[0];
		}

        public Boolean GetBool(int offset)
		{
			if (GetBytes(offset, 1)[0] == 0)
				return false;
			else
				return true;
		}

		public char GetChar(int offset)
		{
			
			return (Character)getPrimitives(GetBytes(offset, 2),'c');
		}
		
		public short GetShort(int offset)
		{
			return (Short)getPrimitives(GetBytes(offset, 2),'s');
		}
		
		public int GetInt(int offset)
		{
			return (Integer)getPrimitives(GetBytes(offset, 4),'i');
		}

		
		
		public long GetLong(int offset)
		{
			return (Long)getPrimitives(GetBytes(offset, 8),'l');
		}


        public double GetDouble(int offset)
		{
			return (Double)getPrimitives(GetBytes(offset, 8),'d');
		}

		public float GetFloat(int offset)
		{
			return (Float)getPrimitives(GetBytes(offset, 4),'f');
		}

		public String GetString(int offset, int length)
		{
			_offset = offset;
			String s = new String(GetBytes(offset, length));
			return s;
			//return Encoding.ASCII.GetString(GetBytes(offset, length));
		}

		public String GetString(int offset)
		{
			//int length = GetInt(offset);
			_offset = offset;
			int length = offset;
			String s = new String(GetBytes(offset, length));
			return s;
			//return Encoding.ASCII.GetString(GetBytes(offset, length));
		}

		public Array getArray(List<?>  list)
		{
			Array array =  	(Array)Array.newInstance(list.getClass(), list.size());
			for(int i =0; i< list.size(); i++)
			{
				Array.set(array, i, list.get(i));
			}
			
			return array;			
		}
		
		
		
		public Array getArrayObject(byte[]  result)
		{
			Array array =  	(Array)Array.newInstance(result.getClass(), result.length);
			for(int i =0; i< result.length; i++)
			{
				Array.set(array, i, result[i]);
			}
			
			return array;			
		}
		
		public byte[] GetBytes(int offset, int length)
		{
			_offset = offset;
			byte[] result = new byte[length];
			int numBytes = 0;
            int sectionOffset = offset;
			
            while (numBytes < length)
			{
				int sectionIndex = (int)Math.floor(offset/(float)SECTION_SIZE);
                if (sectionOffset != 0)
                    sectionOffset -= sectionIndex * SECTION_SIZE;
                int cnt = Math.min(SECTION_SIZE - sectionOffset, length - numBytes);

                BlockCopy(getArrayObject(_sections.get(sectionIndex)), sectionOffset, getArrayObject(result), numBytes, cnt);

                sectionOffset = 0;
                _offset += cnt;
				numBytes += cnt;
			}
			
			return result;
		}

		public byte[] ToBytes()
		{
            int numBytes = 0;
            byte[] bytes = new byte[getLength()];

            for (int i = 0; i <= _curSectionIndex; i++)
            {
                int sectionBytes = SECTION_SIZE;
                if (i == _curSectionIndex)
                    sectionBytes = _curIndex;
                BlockCopy(getArray(_sections), 0, getArrayObject(bytes), numBytes, sectionBytes);
                numBytes += sectionBytes;
            }
			return bytes;
		}

		public void FromBytes(byte[] bytes)
		{
			Clear();
			Add(bytes);
		}

		public Object getPrimitives(byte[] bytes, char type)
		{
			ByteArrayInputStream bis;
			DataInputStream dis;
			Object val;
			try{
				bis = new ByteArrayInputStream(bytes);
				dis = new DataInputStream(bis);
				switch(type)
				{
				case 'c': 
					val = dis.readChar(); break;
				case 's':
					val = dis.readShort(); break;
				case 'i':
					val = dis.readInt(); break;
				case 'f':
					val = dis.readFloat(); break;
				case 'l':
					val = dis.readInt(); break;
				case 'd':
					val = dis.readDouble(); break;						
				default:
					val = "null";				
				}				
				bis.close();
				dis.close();				
			}catch(Exception e)
			{								
				return null;
			}
			return val;
			
		}
		
		public Object getUInt(byte[] bytes, int offset)
		{
			ByteArrayInputStream bis;
			DataInputStream dis;
			Object val;
			try{
				bis = new ByteArrayInputStream(bytes);
				dis = new DataInputStream(bis);				
				val = dis.read(bytes, offset, 2);							
				bis.close();
				dis.close();				
			}catch(Exception e)
			{								
				return null;
			}
			return val;
			
		}
		
		static public String GetString(byte[] bytes, int offset, Boolean isNullTerminated)
		{
			
			int length = (Integer)(new ByteList().getUInt(bytes, offset));
			offset += 2;
			String result = new String(bytes, offset, length);
			offset += length;
			if (isNullTerminated)
				offset++;
			return result;
		}		  		 
	}