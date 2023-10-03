/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cpabe;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author admin
 */
public class OwnerReceiver extends Thread
{
    OwnerFrame ownf;
    String oid;
    int opt;
    OwnerReceiver(OwnerFrame oe,String id)
    {
        ownf=oe;
        oid=id;
        opt=Integer.parseInt(oid)+9000;                
    }
    
    public void run()
    {
        try
        {
            DatagramSocket ds=new DatagramSocket(opt);
            
            String ms1="Owner#"+oid+"#"+ownf.OName+"#"+ownf.stype;
            byte bt[]=ms1.getBytes();
            DatagramPacket dp1=new DatagramPacket(bt,0,bt.length,InetAddress.getByName("127.0.0.1"),8000);
            ds.send(dp1);
            
            while(true)
            {
                byte data[]=new byte[60000];
                DatagramPacket dp=new DatagramPacket(data,0,data.length);
                ds.receive(dp);
                
                String str=new String(dp.getData()).trim();
                String req[]=str.split("#");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
