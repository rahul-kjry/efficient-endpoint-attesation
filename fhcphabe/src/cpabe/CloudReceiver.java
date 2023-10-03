/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cpabe;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
/**
 *
 * @author admin
 */
public class CloudReceiver extends Thread
{
    CloudFrame cf;
    CloudReceiver(CloudFrame ce)
    {
        cf=ce;
    }
    
    public void run()
    {
        try
        {
             DatagramSocket ds=new DatagramSocket(8000);
            while(true)
            {
                byte data[]=new byte[60000];
                DatagramPacket dp=new DatagramPacket(data,0,data.length);
                ds.receive(dp);
                
                String str=new String(dp.getData()).trim();
                String req[]=str.split("#");
                
                if(req[0].equals("Owner"))
                {
                    DefaultTableModel dm=(DefaultTableModel)cf.jTable1.getModel();
                    Vector v=new Vector();
                    v.add(req[1]);
                    v.add(req[2]);
                    v.add(req[3]);
                    dm.addRow(v);                    
                }//Owner
                
                if(req[0].equals("User"))
                {
                    DefaultTableModel dm=(DefaultTableModel)cf.jTable2.getModel();
                    Vector v=new Vector();
                    v.add(req[1]);
                    v.add(req[2]);
                    v.add(req[3]);
                    dm.addRow(v);                    
                }//User
                
                if(req[0].equals("FileInfo"))
                {
                    DefaultTableModel dm=(DefaultTableModel)cf.jTable3.getModel();
                    Vector v=new Vector();
                    v.add(req[1]);
                    v.add(req[2]);
                    v.add(req[3]);
                    v.add(req[4]);
                    v.add(req[5]);
                    dm.addRow(v);                    
                }//FileInfo
                
                if(req[0].equals("PolicyInfo"))
                {
                     DefaultTableModel dm=(DefaultTableModel)cf.jTable4.getModel();
                    Vector v=new Vector();
                    v.add(req[1]);
                    v.add(req[2]);
                    v.add(req[3]);
                    v.add(req[4]);
                    v.add(req[5]);
                    v.add(req[6]);
                    dm.addRow(v);   
                } //PolicyInfo
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
