package com.example.amplaybyalmamun.gadgets.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurUtil {

    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        RenderScript renderScript = RenderScript.create(context);

        // Create an allocation from the input bitmap
        Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        // Create a blur script
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(radius); // max radius is 25

        // Run the blur script
        script.forEach(output);

        // Copy the output to a new bitmap
        Bitmap blurredBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        output.copyTo(blurredBitmap);

        // Release resources
        renderScript.destroy();

        return blurredBitmap;
    }

    public static Bitmap blur(Context context, int drawableResId, float radius) {
        Bitmap inputBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        return blur(context, inputBitmap, radius);
    }
}
