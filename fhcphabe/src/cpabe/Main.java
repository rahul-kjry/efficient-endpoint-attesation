/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cpabe;

/**
 *
 * @author admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CloudFrame cf=new CloudFrame();
        cf.setVisible(true);
        cf.setResizable(false);
        cf.setTitle("Cloud Storage");
        
        CloudReceiver cr = new CloudReceiver(cf);
        cr.start();
        
        LoginFrame lf = new LoginFrame();
        lf.setVisible(true);
        lf.setTitle("Login");
        lf.setResizable(false);
        
    }
    
}
