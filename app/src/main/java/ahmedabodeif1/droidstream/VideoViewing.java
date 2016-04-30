package ahmedabodeif1.droidstream;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class VideoViewing extends AppCompatActivity {

    // UI variables
    ImageView imgView;
    TextView statusText;
    Bitmap bmp;

    // Network variables
    Socket sock;
    String URL = "10.0.2.2";
    int portNum = 444;
    String NetworkTag = "SETTING UP NETWORK CONNECTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setConnection();
        setContentView(R.layout.activity_video_viewing);
        imgView = (ImageView) findViewById(R.id.imageView);
        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText("Fetching feed from server");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new ServerConnection().execute();
    }


    private class ServerConnection extends AsyncTask<Void, Void, Bitmap>
    {
        // each pixel is rep by 8 bits
        Bitmap.Config bmpConfig = Bitmap.Config.ARGB_8888;
        int width = 720;
        int heigth = 480;
        int numberOfBytes = 864000;
        Bitmap result ;
        byte imgData[] = new byte[numberOfBytes];

        char chars[] = new char[numberOfBytes];

        BufferedInputStream serverStreamRedaer;
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        BufferedOutputStream out;
        Socket sock;


        String SUCCESS = "CONNECTED TO SERVER";
        BufferedReader inFromServer;
        String msgFromServer = "Empty";


        public void setConnection()
        {
            try
            {
                InetAddress address = InetAddress.getByName(URL);
                sock = new Socket(address,portNum);
                /*
                if(sock.isConnected())
                    Log.e(NetworkTag, "It got connected");
                else
                    Log.e(NetworkTag, "Not connected");
                    */
            }
            catch (UnknownHostException e) {
                Log.e(NetworkTag, "Could not connect to host");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.e(NetworkTag, "IOException");
                e.printStackTrace();
            }
            catch(Exception e) {
                Log.e(NetworkTag, "Unknown Exception Thrown, perhaps", e );
            }
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            setConnection();
            getImageStream();
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
            new ServerConnection().execute();
        }

        public void getImageStream()
        {
            try
            {
                InputStream in = sock.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                int remainingBytes = numberOfBytes;
                while(remainingBytes > 0)
                {
                    int bytesRead = in.read(buffer);
                    if(bytesRead < 0)
                    {
                        throw new IOException("unexpected end of data");
                    }
                    baos.write(buffer,0,bytesRead);
                    remainingBytes -= bytesRead;
                }
                in.close();
                imgData = baos.toByteArray();
                baos.close();

                int numberOfPixels = numberOfBytes/3;
                int pixels[] = new int [numberOfPixels];
                for(int i=0; i<numberOfPixels; i++)
                {
                    int b = imgData[i*3];
                    int g = imgData[i*3 + 1];
                    int r = imgData[i*3 + 2];
                    if(r < 0)
                        r += 256;
                    if(g < 0)
                        g += 256;
                    if(b < 0 )
                        b += 256;
                    pixels[i] = Color.rgb(
                            r,g,b);
                }

                result = Bitmap.createBitmap(pixels,720,400, Bitmap.Config.ARGB_8888);
                imgData = null;

                // Log.e(NetworkTag, "I should have the imgStream filled by now");
                //  convert the vytes to pixels;
            }
            catch (IOException e)
            {
                Log.e(NetworkTag, "IO Exception thrown during receiving image byte stream.");
            }
            catch(Exception e)
            {
                Log.e(NetworkTag, "Uknown Exception thrown during receiving image byte stream.");
            }

        }
    }
}

