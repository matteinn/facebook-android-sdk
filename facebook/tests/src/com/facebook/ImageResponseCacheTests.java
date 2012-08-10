/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

public final class ImageResponseCacheTests extends AndroidTestCase {

    @MediumTest @LargeTest
    public void testImageCaching() throws IOException {
        // In unit test, since we need verify first access the image is not in cache
        // we need clear the cache first
        ImageResponseCache.getCache(this.getContext()).clear();
        String imgUrl = "http://sphotos-b.xx.fbcdn.net/hphotos-snc7/300716_163831917043403_1106723719_n.jpg";
        
        Bitmap bmp1 = readImage(imgUrl, false);
        Bitmap bmp2 = readImage(imgUrl, true);
        compareImages(bmp1, bmp2);
    }
    
    @MediumTest @LargeTest
    public void testImageNotCaching() throws IOException {
        
        String imgUrl = "http://graph.facebook.com/ryanseacrest/picture?type=large";
        
        Bitmap bmp1 = readImage(imgUrl, false);
        Bitmap bmp2 = readImage(imgUrl, false);
        compareImages(bmp1, bmp2);
    }

    private Bitmap readImage(String url, boolean expectedFromCache) {
        Bitmap bmp = null;
        InputStream istream = null;
        BufferedInputStream bis = null;
        try
        {
            // Check if the cache contains value for this url
            boolean isInCache = (ImageResponseCache.getCache(this.getContext()).get(url) != null);
            assertTrue(isInCache == expectedFromCache);
            // Read the image
            istream = ImageResponseCache.getImageStream(url, this.getContext());
            assertTrue(istream != null);
            bis = new BufferedInputStream(istream);
            bmp = BitmapFactory.decodeStream(bis);
            assertTrue(bmp != null);
        } catch (Exception e) {
             assertNull(e);
        } finally {
            Utility.closeQuietly(istream);
            Utility.closeQuietly(bis);
           
        }
        return bmp;
    }
    
    private static void compareImages(Bitmap bmp1, Bitmap bmp2) {
        assertTrue(bmp1.getHeight() == bmp2.getHeight());
        assertTrue(bmp1.getWidth() == bmp1.getWidth());
        ByteBuffer buffer1 = ByteBuffer.allocate(bmp1.getHeight() * bmp1.getRowBytes());
        bmp1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bmp2.getHeight() * bmp2.getRowBytes());
        bmp2.copyPixelsToBuffer(buffer2);

        assertTrue(Arrays.equals(buffer1.array(), buffer2.array()));
    }
}