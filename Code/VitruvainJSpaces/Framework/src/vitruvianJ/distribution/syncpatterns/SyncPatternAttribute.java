package vitruvianJ.distribution.syncpatterns;

import java.text.Annotation;

//package vitruvianJ.serialization;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD , ElementType.METHOD})
public @interface SyncPatternAttribute {
 String getSyncPatternId();
}

/*
 public class SyncPatternAttribute extends Annotation
	{
		private String _syncPatternId = "";

		/// <summary>
		/// Construct a SyncPatternAttribute.
		/// </summary>
		/// <param name="syncPatternId">The id of the sync pattern.</param>

		public SyncPatternAttribute(String syncPatternId)
		{
			super(syncPatternId);
			_syncPatternId = syncPatternId;
		}

		/// <summary>
		/// The id of the sync pattern.
		/// </summary>
		public String getSyncPatternId()
		{
			return _syncPatternId; 
		}
		public void	setSyncPatternId(String value) { _syncPatternId = value; }
		
	}
*/