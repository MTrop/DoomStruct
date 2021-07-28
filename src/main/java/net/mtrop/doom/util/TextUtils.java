package net.mtrop.doom.util;

import java.nio.charset.Charset;

/**
 * Text utilities and constants.
 * @author Matthew Tropiano
 * @since 2.13.0
 */
public final class TextUtils 
{
	/** ASCII encoding. */
	public static final Charset ASCII = Charset.forName("ASCII");
	/** CP437 encoding (the extended MS-DOS charset). */
	public static final Charset CP437 = Charset.forName("CP437");
	/** UTF-8 encoding. */
	public static final Charset UTF8 = Charset.forName("UTF-8");

	private TextUtils() {}

}
