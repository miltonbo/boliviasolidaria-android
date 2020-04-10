package com.jcode.apps.boliviasolidaria.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

@SuppressWarnings("unused")
public class ImageManager {

	public static Bitmap redimensionarImagen(Bitmap mBitmap, float newWidth, float newHeigth) {
		// Defino las dimensiones
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeigth) / height;
		// Creo matriz para manipular tamanos
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// Reconstruyo el bitmap
		return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
	}

	public static byte[] toByteArray(Bitmap bitmap) {
		// ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// byte[] result = bos.toByteArray();
		// return result;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
		bitmap.compress(CompressFormat.JPEG, 80, bos);
		return bos.toByteArray();
	}

	public static byte[] toByteArray(Bitmap bitmap, int quality) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
		bitmap.compress(CompressFormat.JPEG, quality, bos);
		return bos.toByteArray();
	}

	public static byte[] compressBitmap(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
		bitmap.compress(CompressFormat.JPEG, 80, bos);
		return bos.toByteArray();
	}

	public static Bitmap toBitmap(byte[] array) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 2;
		Bitmap image = null;
		try {

			image = BitmapFactory.decodeByteArray(array, 0, array.length, options);
			array = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromArray(byte[] arrayImage, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length, options);
	}
	
	public static Bitmap decodeSampledBitmapForCameraFromArray(byte[] arrayImage, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length, options);
		// Calculate inSampleSize
		options.inSampleSize = 2;
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(arrayImage, 0, arrayImage.length, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(String Path, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(Path, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(Path, options);
	}

}
