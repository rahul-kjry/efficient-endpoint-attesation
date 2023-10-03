/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cpabe;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.LinkedList;
import java.text.DecimalFormat;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.FileAttribute;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
/**
 *
 * @author admin
 */
public class OwnerFrame extends javax.swing.JFrame {

    /**
     * Creates new form OwnerFrame
     */
    String OId;
    String OName;
    String stype;
    double storage=0;
    double avlstorage=0;
    double cost=0;
    
  
    
    public String attr="Occupation:Physician Occupation:Nurse Occupation:Pharmacist "+
            "Medical:Internal_Medicine Medical:Gerontology Medical:General "+
            "Organization:Hospital_A Organization:Hospital_B Organization:Pharmacy_C";
    
    Datacenter dc1;
    DatacenterCharacteristics characteristics;
    Vm vm1;
    BwProvisionerSimple BwPro;
    RamProvisionerSimple StePro ;           
            
    public OwnerFrame(String id,String name,String type) 
    {
        initComponents();
        OId=id;
        OName=name;        
        stype=type;
        display();
        createStorage();
    }

    public void display()
    {
        try
        {
            DBConnection db=new DBConnection();
            Statement st=db.stt;
            ResultSet rs=st.executeQuery("select * from storageinfo where OId='"+OId+"' and OName='"+OName+"'");
            if(rs.next())
            {
                storage=rs.getDouble(3);
                avlstorage=rs.getDouble(4);
                cost=rs.getDouble(5);
                jLabel2.setText("Storage Capacity - "+storage);
                jLabel9.setText("Available - "+avlstorage);
                jLabel10.setText("Cost - "+cost);
            }
            
            ResultSet rs2=st.executeQuery("select FileName from fileinfo where OId='"+OId+"' and OName='"+OName+"'");
            while(rs2.next())
            {
                String fn=rs2.getString(1);
                jComboBox1.addItem(fn);
                jComboBox2.addItem(fn);
            }
            
            ResultSet rs3=st.executeQuery("select * from policy where OId='"+OId+"' and OName='"+OName+"'");
            
            DefaultTableModel dm=(DefaultTableModel)jTable1.getModel();
            while(rs3.next())
            {
                Vector v=new Vector();
                v.add(rs3.getString(3));
                v.add(rs3.getString(4));
                v.add(rs3.getString(5));
                v.add(rs3.getString(6));
                dm.addRow(v);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createStorage()
    {
        try
        {
            Log.printLine("Starting CloudSim");
            CloudSim cs=new CloudSim();
            Calendar calendar = Calendar.getInstance();
            cs.init(1, calendar,false);
            
            String name="DC1";
            List<Host> hostList = new ArrayList<Host>();
            List<Pe> peList1 = new ArrayList<Pe>();
            int mips1 = 1000000;
            peList1.add(new Pe(0, new PeProvisionerSimple(mips1))); 
            List<Pe> peList2 = new ArrayList<Pe>();
            peList2.add(new Pe(0, new PeProvisionerSimple(mips1)));
            
            int hostId = 0;
            int ram1 = 65536; // host memory (MB)
            long storage = 1000000; // host storage
            int bw2 = 1000000;

            hostList.add(new Host(hostId, new RamProvisionerSimple(ram1),new BwProvisionerSimple(bw2), storage, peList1,new VmSchedulerTimeShared(peList1))); 
						
            String arch = "x86"; // system architecture
            String os = "Linux"; // operating system
            String vmm1 = "Xen";
            double time_zone = 10.0; // time zone this resource located
            double cost = 3.0; // the cost of using processing in this resource
            double costPerMem = 0.05; // the cost of using memory in this resource
            double costPerStorage = 0.2; // the cost of using storage in this
										// resource
            double costPerBw = 0.1; // the cost of using bw in this resource
            LinkedList<Storage> storageList = new LinkedList<Storage>();
            
            characteristics = new DatacenterCharacteristics(arch, os, vmm1, hostList, time_zone, cost, costPerMem,costPerStorage, costPerBw);

            dc1 = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
            System.out.println("Data Center Created");
            
            int vmid = Integer.parseInt(OId);
            int cid=Integer.parseInt(OId);
            int mips = 250;
            long size = 10000; //image size (MB)
            int ram = (int)avlstorage;
            long bw =(long) avlstorage;
            int pesNumber = 1; //number of cpus
            String vmm = "Xen"; //VMM name

            vm1 = new Vm(vmid,cid, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            BwPro=new BwProvisionerSimple(bw);
                
           StePro=new RamProvisionerSimple(ram);
            
            System.out.println("VM-"+vmid+" is Created... with "+bw+" BandWidth & "+ram+" Storage Capacity");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jButton4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Calisto MT", 0, 24)); // NOI18N
        jLabel1.setText("Data Owner");

        jTabbedPane1.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel2.setText("Storage Capacity  -");

        jLabel9.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel9.setText("Available Storage -");

        jLabel10.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel10.setText("Cost -");

        jButton5.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jButton5.setText("Deallocate Storage");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(jButton5)))
                .addContainerGap(299, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel2)
                .addGap(45, 45, 45)
                .addComponent(jLabel9)
                .addGap(46, 46, 46)
                .addComponent(jLabel10)
                .addGap(59, 59, 59)
                .addComponent(jButton5)
                .addContainerGap(151, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Info", jPanel6);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel3.setText("Select");

        jTextField1.setEditable(false);

        jButton1.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jButton1.setText("Browse");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jButton2.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jButton2.setText("Store");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(49, 49, 49)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(335, 335, 335)
                        .addComponent(jButton2)))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addGap(27, 27, 27))
        );

        jTabbedPane1.addTab("Store File", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel4.setText("Select");

        jComboBox1.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N

        jButton3.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jButton3.setText("View");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(jLabel4)
                        .addGap(48, 48, 48)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jButton3))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        jTabbedPane1.addTab("View File", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel5.setText("Select File");

        jComboBox2.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel6.setText("Profession");

        jLabel7.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel7.setText("Medical Specialty");

        jComboBox3.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Physician", "Nurse", "Pharmacist" }));

        jComboBox4.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Internal_Medicine", "Gerontology", "General" }));

        jLabel8.setFont(new java.awt.Font("Monospaced", 0, 17)); // NOI18N
        jLabel8.setText("Organization");

        jComboBox5.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hospital_A", "Hospital_B", "Pharmacy_C" }));

        jButton4.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jButton4.setText("Add Access Policy");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(80, 80, 80)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox5, 0, 196, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, 196, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(224, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(327, 327, 327))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addComponent(jButton4)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Access Policy", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setFont(new java.awt.Font("Monospaced", 0, 16)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "Profession", "Medical", "Organization"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(26);
        jScrollPane4.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(77, 77, 77)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(78, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(83, 83, 83)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(57, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("View AP", jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 777, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(353, 353, 353))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try
        {
            JFileChooser fc=new JFileChooser();
            int ch=fc.showOpenDialog(this);
            if(ch==JFileChooser.APPROVE_OPTION)
            {
                String path=fc.getSelectedFile().getAbsolutePath();
                jTextField1.setText(path);
                File fe=new File(path);
                FileInputStream fis=new FileInputStream(fe);
                byte data[]=new byte[fis.available()];
                fis.read(data);
                fis.close();

                String cnt=new String(data);
                jTextArea2.setText(cnt);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try
        {
            DecimalFormat df=new DecimalFormat("#.###");
            String path=jTextField1.getText().trim();
            String cnt=jTextArea2.getText().trim();
            
            if(path.equals("")||cnt.equals(""))
                JOptionPane.showMessageDialog(this, "Select File");
            else
            {
                File fe=new File(path);
                String fname=fe.getName();
                
                DBConnection db=new DBConnection();
                Statement st=db.stt;
                ResultSet rs=st.executeQuery("select * from fileinfo where OId='"+OId+"' and OName='"+OName+"' and FileName='"+fname+"'");
                if(rs.next())
                    JOptionPane.showMessageDialog(this,"File already stored...");
                else
                {
                    // check avl storage
                    FileInputStream fis=new FileInputStream(fe);
                    byte data[]=new byte[fis.available()];
                    fis.read(data);
                    fis.close();
                    
                    int dLen=data.length;
                    double be=dLen/1000;
                    int sz=(int)be/1000;
                    if(sz<1)
                        sz=1;
                    System.out.println(dLen+" : "+sz+" : "+be);
                    org.cloudbus.cloudsim.File cfile=new org.cloudbus.cloudsim.File(fname,sz);
                    FileAttribute fatt=new FileAttribute(fname,sz);
                    boolean bool1=BwPro.allocateBwForVm(vm1, dLen);
                    boolean bool2=StePro.allocateRamForVm(vm1, dLen);
                    
                    if(bool1 && bool2)
                    {
                        dc1.addFile(cfile);
                        
                        double ct1=characteristics.getCostPerStorage()*dLen;
                        System.out.println(ct1);
                        cost=cost+ct1;
                        avlstorage=StePro.getAvailableRam();
                        jLabel9.setText("Available - "+avlstorage);
                        jLabel10.setText("Cost - "+df.format(cost));
                        
                        st.executeUpdate("update storageinfo set Storage='"+storage+"' , AvlStorage='"+avlstorage+"', Cost='"+df.format(cost)+"' where OId='"+OId+"' and OName='"+OName+"'");
                        
                        String ins_file = "insert into fileinfo(OId, OName, FileName, FileCnt, SCap, Cost) values (?, ?, ?, ?, ?, ?)";
                 
                        PreparedStatement ps1 = db.con.prepareStatement(ins_file);
                        ps1.setString(1, OId);
                        ps1.setString(2, OName);
                        ps1.setString(3, fname);
                        ps1.setBytes(4, data);
                        ps1.setString(5, String.valueOf(dLen));
                        ps1.setString(6, String.valueOf(cost));
                    
                        ps1.executeUpdate();
                        
                        
                        jComboBox1.addItem(fname);
                        jComboBox2.addItem(fname);
                        DatagramSocket ds=new DatagramSocket();
                        String ms="FileInfo#"+OId+"#"+OName+"#"+fname+"#"+dLen+"#"+ct1;
                        byte bt[]=ms.getBytes();
                        DatagramPacket dp=new DatagramPacket(bt,0,bt.length,InetAddress.getByName("127.0.0.1"),8000);
                        ds.send(dp);
                        
                        JOptionPane.showMessageDialog(this, "File Stored...");
                        //System.out.println("avl "+BwPro.getAvailableBw()+" : "+StePro.getAvailableRam());
                        //System.out.println("used "+BwPro.getUsedBw()+" : "+StePro.getUsedRam());
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Storage Capacity is not enough to store file");
                    }
                    
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try
        {
            // deallocate 
            if(stype.equals("Small"))
            {
                storage=2000;
                avlstorage=2000;
                cost=0;
            }
            else if(stype.equals("Medium"))
            {
                storage=5000;
                avlstorage=5000;
                cost=0;
            }
            else
            {
                storage=10000;
                avlstorage=10000;
                cost=0;
            }
            
            int vmid = Integer.parseInt(OId);
            int cid=Integer.parseInt(OId);
            int mips = 250;
            long size = 10000; //image size (MB)
            int ram = (int)avlstorage;
            long bw =(long) avlstorage;
            int pesNumber = 1; //number of cpus
            String vmm = "Xen"; //VMM name

            vm1 = new Vm(vmid,cid, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            BwPro=new BwProvisionerSimple(bw);
                
           StePro=new RamProvisionerSimple(ram);
           
           DBConnection db=new DBConnection();
           Statement st=db.stt;
           st.executeUpdate("update storageinfo set Storage='"+storage+"' , AvlStorage='"+avlstorage+"', Cost='"+cost+"' where OId='"+OId+"' and OName='"+OName+"'");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try
        {
            // View File
            DBConnection db=new DBConnection();
            Statement st=db.stt;
            
            String fname=jComboBox1.getSelectedItem().toString();
            
            ResultSet rs=st.executeQuery("select FileCnt from fileinfo where FileName='"+fname+"'");
            if(rs.next())
            {
                byte b1[]=rs.getBytes(1);
                jTextArea3.setText(new String(b1));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        try
        {
            // Access policy
            String fname=jComboBox2.getSelectedItem().toString();
            String pro1=jComboBox3.getSelectedItem().toString().trim();
            String med1=jComboBox4.getSelectedItem().toString().trim();
            String org1=jComboBox5.getSelectedItem().toString().trim();
            
            DBConnection db=new DBConnection();
            Statement st=db.stt;
            ResultSet rs=st.executeQuery("select * from Policy where OId='"+OId+"' and OName='"+OName+"' and FileName='"+fname+"'"); 
            if(rs.next())
            {
                JOptionPane.showMessageDialog(this, "Access Policy Already Added...");
            }
            else
            {
              
                
                String pubfile = "pub_key";
                String mskfile = "master_key";
                String prvfile = "prv_key";
                
                Cpabe cp1 = new Cpabe();
                cp1.setup(pubfile, mskfile);
                cp1.keygen(pubfile, prvfile, mskfile, attr);
                
                
                ResultSet rs2=st.executeQuery("select FileCnt from fileinfo where OId='"+OId+"' and OName='"+OName+"' and FileName='"+fname+"'");
                if(rs2.next())
                {
                    byte b1[]=rs2.getBytes(1);                
                    File fe=new File(fname);
                    FileOutputStream fos=new FileOutputStream(fe);
                    fos.write(b1);
                    fos.close();
                    
                    String policy="Medical:"+med1+" Occupation:"+pro1+" Organization:"+org1+" 3of3";
                   // String policy="Medical:"+med1+" Occupation:"+pro1+" 2of2";
                    cp1.enc(pubfile, policy, fe.getAbsolutePath(), "enc1.txt");
                }
                 
                
                    
                  st.executeUpdate("insert into Policy values('"+OId+"','"+OName+"','"+fname+"','"+pro1+"','"+med1+"','"+org1+"')");
                  
                File fe2=new File("enc1.txt");
                FileInputStream fis=new FileInputStream(fe2);
                byte bt[]=new byte[fis.available()];
                fis.read(bt);
                fis.close();
                    
                    
                File ky1=new File(pubfile);
                FileInputStream fispub=new FileInputStream(ky1);
                byte btpub[]=new byte[fispub.available()];
                fispub.read(btpub);
                fispub.close();
                    
                File ky2=new File(mskfile);
                FileInputStream fisms=new FileInputStream(ky2);
                byte btms[]=new byte[fisms.available()];
                fisms.read(btms);
                fisms.close();
                   
                   
                File ky3=new File(prvfile);
                FileInputStream fisprv=new FileInputStream(ky3);
                byte btprv[]=new byte[fisprv.available()];
                fisprv.read(btprv);
                fisprv.close();
                    
                String ins_server = "insert into filekeys(OId, OName, FileName, EncFile, PubKey, MasterKey, PrivKey) values (?, ?, ?, ?, ?, ?, ?)";
                 
                PreparedStatement ps1 = db.con.prepareStatement(ins_server);
                ps1.setString(1, OId);
                ps1.setString(2, OName);
                ps1.setString(3, fname);
                ps1.setBytes(4, bt);
                ps1.setBytes(5, btpub);
                ps1.setBytes(6, btms);
                ps1.setBytes(7, btprv);
                    
                ps1.executeUpdate();     
                
                DefaultTableModel dm=(DefaultTableModel)jTable1.getModel();
                Vector v1=new Vector();
                v1.add(fname);
                v1.add(pro1);
                v1.add(med1);
                v1.add(org1);
                dm.addRow(v1);
                        
                
                DatagramSocket ds=new DatagramSocket();
                String ms="PolicyInfo#"+OId+"#"+OName+"#"+fname+"#"+pro1+"#"+med1+"#"+org1;
                byte bt1[]=ms.getBytes();
                DatagramPacket dp=new DatagramPacket(bt1,0,bt1.length,InetAddress.getByName("127.0.0.1"),8000);
                ds.send(dp);
                
                JOptionPane.showMessageDialog(this, "Access Policy Added...");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OwnerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OwnerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OwnerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OwnerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               // new OwnerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    public javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
