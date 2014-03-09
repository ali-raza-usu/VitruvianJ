package vitruvianJ.content;


    /// <summary>
    /// An interface that can load resources.
    /// </summary>
	public interface IContentLoader
	{
		/// <summary>
		/// Initialize the loader.
		/// </summary>
		void Init();

		/// <summary>
		/// Cleanup the loader.
		/// </summary>
		void Cleanup();

        /// <summary>
        /// Determine if the loader can handle the content.
        /// </summary>
        /// <param name="contentType"></param>
        /// <returns></returns>
        boolean CanLoad(String contentType);

		/// <summary>
		/// Load the content using the resource id.
		/// </summary>
		/// <param name="resource">The information locating the resource.</param>
		/// <returns>The object that was loaded.</returns>
		Object Load(String resource);

        /// <summary>
        /// Load the content using the resource id.
        /// </summary>
        /// <param name="resource">The information locating the resource.</param>
        void Load(String resource, Object value);
	}
