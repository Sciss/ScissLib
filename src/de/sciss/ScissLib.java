package de.sciss;

//import java.io.File;
//import java.io.IOException;
//import de.sciss.io.AudioFile;

public class ScissLib
{
	private static final double VERSION	= 0.12;
	
	public static void main( String[] args )
	{
		System.err.println( "\nScissUtil v" + VERSION + "\n" +
		    				"(C)opyright 2004-2010 by Hanns Holger Rutz. All rights reserved.\n" +
		    				"Published under the GNU General Public License.\n\n" +
		    				"This is a library which is not meant to be executed by itself.\n\n" );
		
//		testAIFF();
		System.exit( 1 );
	}

	/**
	 *	Returns the library's version.
	 *
	 *	@return	the current version of ScissUtil
	 */
	public static final double getVersion()
	{
		return VERSION;
	}
	
//	private static void testAIFF()
//	{
//		try {
//			AudioFile.openAsRead( new File( "/Users/rutz/Kontur/cache/32AF4200.cache" ));
//		}
//		catch( IOException e1 ) {
//			e1.printStackTrace();
//		}
//	}
}
