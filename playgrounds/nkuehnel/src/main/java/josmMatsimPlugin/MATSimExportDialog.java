/**
 * 
 */
package josmMatsimPlugin;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
 * Main export dialog
 * @author nkuehnel
 *
 */
public class MATSimExportDialog extends JPanel {
	// the JOptionPane that contains this dialog. required for the closeDialog() method.
    private JOptionPane optionPane;
    private JCheckBox originalData;
    private JComboBox coordCombo;
    static JTextField path;
    private JButton fileChooser;
    

    public MATSimExportDialog() {
        GridBagConstraints c = new GridBagConstraints();

        setLayout(new GridBagLayout());
        String[] coordSystems = { TransformationFactory.WGS84, TransformationFactory.ATLANTIS, TransformationFactory.CH1903_LV03,TransformationFactory.GK4, TransformationFactory.WGS84_UTM47S, TransformationFactory.WGS84_UTM48N, TransformationFactory.WGS84_UTM35S,TransformationFactory.WGS84_UTM36S,TransformationFactory.WGS84_Albers,TransformationFactory.WGS84_SA_Albers,TransformationFactory.WGS84_UTM33N,TransformationFactory.DHDN_GK4,TransformationFactory.WGS84_UTM29N,TransformationFactory.CH1903_LV03_GT,TransformationFactory.WGS84_SVY21,TransformationFactory.NAD83_UTM17N,TransformationFactory.WGS84_TM};
        coordCombo = new JComboBox(coordSystems);
      
        c.insets = new Insets(4,4,4,4);
        c.gridwidth = 1;
        c.weightx = 0.8;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel(tr("Coordinate System:")), c);

        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.5;
        add(coordCombo, c);
        
        coordCombo.addActionListener(new ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
            	ExportTask.targetSystem=(String) coordCombo.getSelectedItem();
            }
        });
        
        c.gridwidth = 1;
        c.weightx = 0.8;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel(tr("Save to:")), c);
        
        
        path=new JTextField(System.getProperty("user.home") + "\\josm_matsim_export");
        c.gridwidth = 2;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.;
        add(path, c);
        
        fileChooser=new JButton("Choose..");
        c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 1.;
        add(fileChooser, c);
        
        fileChooser.addActionListener(new ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
            	JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
            	chooser.setDialogTitle("MATSim-Export");
            	chooser.setApproveButtonText("Confirm");
            	FileFilter filter = new FileNameExtensionFilter("Network-XML", "xml");
            	chooser.setFileFilter(filter);
            	File file = new File(System.getProperty("user.home")+"/josm_matsim_export");
            	chooser.setSelectedFile(file);
            	int result= chooser.showSaveDialog(null);
            	if (result==JFileChooser.APPROVE_OPTION && chooser.getSelectedFile().getAbsolutePath()!=null)
            		path.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
       

        originalData = new JCheckBox(tr("save original osm data"));
        originalData.setSelected(false);
        

        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        add(originalData, c);
        
        originalData.addActionListener(new ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
//            	if (originalData.isSelected())
//            		ExportTask.keepOriginal=true;
//            	else
//            		ExportTask.keepOriginal=false;
            }
        });
    }

    public void setOptionPane(JOptionPane optionPane) {
        this.optionPane = optionPane;
    }
}