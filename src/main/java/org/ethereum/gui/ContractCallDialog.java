package org.ethereum.gui;

import org.ethereum.core.Transaction;
import org.ethereum.manager.MainData;
import org.ethereum.net.client.ClientPeer;
import org.ethereum.util.ByteUtil;
import org.ethereum.util.Utils;
import org.ethereum.wallet.AddressState;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.encoders.Hex;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;


/**
 * www.ethereumJ.com
 * User: Roman Mandeleil
 * Created on: 18/05/14 22:21
 */
class ContractCallDialog extends JDialog implements MessageAwareDialog{


    ContractCallDialog dialog;
    JComboBox<AddressStateWraper> creatorAddressCombo;
    final JTextField gasInput;
    final JTextField contractAddrInput;
    JTextArea   msgDataTA;



    JLabel statusMsg = null;

    public ContractCallDialog(Frame parent) {
        super(parent, "Call Contract: ", false);
        dialog = this;

        contractAddrInput = new JTextField(5);
        GUIUtils.addStyle(contractAddrInput, "Contract Address: ");

        contractAddrInput.setBounds(70, 30, 350, 45);
        this.getContentPane().add(contractAddrInput);

        gasInput = new JTextField(5);
        GUIUtils.addStyle(gasInput, "Gas: ");

        msgDataTA = new JTextArea();
        msgDataTA.setLineWrap(true);
        JScrollPane contractDataInput = new JScrollPane(msgDataTA);
        GUIUtils.addStyle(msgDataTA, null, false);
        GUIUtils.addStyle(contractDataInput, "Data:");

        msgDataTA.setText("");
        msgDataTA.setCaretPosition(0);

        this.getContentPane().setBackground(Color.WHITE);
        this.getContentPane().setLayout(null);

        contractDataInput.setBounds(70, 80, 350, 165);
        this.getContentPane().add(contractDataInput);

        gasInput.setBounds(330, 260, 90, 45);
        this.getContentPane().add(gasInput);

        URL rejectIconURL = ClassLoader.getSystemResource("buttons/reject.png");
        ImageIcon rejectIcon = new ImageIcon(rejectIconURL);
        JLabel rejectLabel = new JLabel(rejectIcon);
        rejectLabel.setToolTipText("Cancel");
        rejectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel statusMessage = new JLabel("");
        statusMessage.setBounds(50, 360, 400, 50);
        statusMessage.setHorizontalAlignment(SwingConstants.CENTER);
        this.statusMsg = statusMessage;
        this.getContentPane().add(statusMessage);

        rejectLabel.setBounds(260, 325, 45, 45);
        this.getContentPane().add(rejectLabel);
        rejectLabel.setVisible(true);
        rejectLabel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        dialog.dispose();
                    }}
        );

        URL approveIconURL = ClassLoader.getSystemResource("buttons/approve.png");
        ImageIcon approveIcon = new ImageIcon(approveIconURL);
        JLabel approveLabel = new JLabel(approveIcon);
        approveLabel.setToolTipText("Submit the transaction");
        approveLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        approveLabel.setBounds(200, 325, 45, 45);
        this.getContentPane().add(approveLabel);
        approveLabel.setVisible(true);

        approveLabel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        submitContract();
                    }
                }
        );

        gasInput.setText("1000");

        JComboBox<AddressStateWraper> creatorAddressCombo = new JComboBox<AddressStateWraper>(){
            @Override
            public ComboBoxUI getUI() {

                BasicComboBoxUI ui = (BasicComboBoxUI)super.getUI();

                return super.getUI();
            }
        };
        creatorAddressCombo.setOpaque(true);
        creatorAddressCombo.setEnabled(true);

        creatorAddressCombo.setBackground(Color.WHITE);
        creatorAddressCombo.setFocusable(false);

        this.creatorAddressCombo = creatorAddressCombo;

        final Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        JComponent editor = (JComponent)(creatorAddressCombo.getEditor().getEditorComponent());
        editor.setForeground(Color.RED);

        Collection<AddressState> addressStates =
                MainData.instance.getWallet().getAddressStateCollection();

        for (AddressState addressState : addressStates){

            creatorAddressCombo.addItem(new AddressStateWraper(addressState));
        }

        creatorAddressCombo.setRenderer(new DefaultListCellRenderer() {

            @Override
            public void paint(Graphics g) {
                setBackground(Color.WHITE);
                setForeground(new Color(143, 170, 220));
                setFont(new Font("Monospaced", 0, 13));
                setBorder(BorderFactory.createEmptyBorder());
                super.paint(g);
            }

        });

        creatorAddressCombo.setPopupVisible(false);

        Object child = creatorAddressCombo.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;

        JList list = popup.getList();
        list.setSelectionBackground(Color.cyan);
        list.setBorder(null);

        for (int i = 0; i < creatorAddressCombo.getComponentCount(); i++)
        {
            if (creatorAddressCombo.getComponent(i) instanceof CellRendererPane) {

                CellRendererPane crp = ((CellRendererPane) (creatorAddressCombo.getComponent(i)));
            }

            if (creatorAddressCombo.getComponent(i) instanceof AbstractButton) {
                ((AbstractButton) creatorAddressCombo.getComponent(i)).setBorder(line);
            }
        }
        creatorAddressCombo.setBounds(73, 267, 230, 36);
        this.getContentPane().add(creatorAddressCombo);


        this.getContentPane().revalidate();
        this.getContentPane().repaint();
        this.setResizable(false);
    }

    protected JRootPane createRootPane() {

        Container parent = this.getParent();

        if (parent != null) {
            Dimension parentSize = parent.getSize();
            Point p = parent.getLocation();
            setLocation(p.x + parentSize.width / 4, p.y + 10);
        }

        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

        this.setSize(500, 430);
        this.setVisible(true);


        return rootPane;
    }

    public void infoStatusMsg(String text){
        this.statusMsg.setForeground(Color.GREEN.darker().darker());
        this.statusMsg.setText(text);
    }

    public void alertStatusMsg(String text){
        this.statusMsg.setForeground(Color.RED);
        this.statusMsg.setText(text);
    }


    public void submitContract(){

        ClientPeer peer = MainData.instance.getActivePeer();
        if (peer == null) {
            dialog.alertStatusMsg("Not connected to any peer");
            return;
        }

        Object[] lexaList = msgDataTA.getText().split(",");
        byte[] data = ByteUtil.encodeDataList(lexaList);

        byte[] contractAddress = Hex.decode( contractAddrInput.getText());


        AddressState addressState = ((AddressStateWraper)creatorAddressCombo.getSelectedItem()).getAddressState();

        byte[] senderPrivKey = addressState.getEcKey().getPrivKeyBytes();
        byte[] nonce = addressState.getNonce() == BigInteger.ZERO ? null : addressState.getNonce().toByteArray();
        byte[] gasPrice = new BigInteger("10000000000000").toByteArray();

        BigInteger gasBI = new BigInteger(gasInput.getText());
        byte[] gasValue  = BigIntegers.asUnsignedByteArray(gasBI);
        byte[] endowment = BigIntegers.asUnsignedByteArray(new BigInteger("1000"));


        Transaction tx = new Transaction(nonce, gasPrice, gasValue,
                contractAddress, endowment, data);

        try {
            tx.sign(senderPrivKey);
        } catch (Exception e1) {

            dialog.alertStatusMsg("Failed to sign the transaction");
            return;
        }


        // SwingWorker
        DialogWorker worker = new DialogWorker(tx, this);
        worker.execute();

    }




    public class AddressStateWraper{

        private AddressState addressState;

        public AddressStateWraper(AddressState addressState) {
            this.addressState = addressState;
        }

        public AddressState getAddressState() {
            return addressState;
        }

        @Override
        public String toString() {
            String addressShort = Utils.getAddressShortString(addressState.getEcKey().getAddress());
            String valueShort   = Utils.getValueShortString(addressState.getBalance());

            String result =
                    String.format(" By: [%s] %s",
                            addressShort, valueShort);

            return result;
        }
    }
}

