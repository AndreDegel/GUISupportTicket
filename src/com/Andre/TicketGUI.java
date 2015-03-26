package com.Andre;

import javax.imageio.IIOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bd2319wv on 3/24/2015.
 */
public class TicketGUI extends JFrame{
    private JTextField txtID;
    private JTextField txtIssue;
    private JTextField txtReportedBy;
    private JTextField txtResolved;
    private JTextField txtReportedOn;
    private JTextField txtResolvedOn;
    private JButton addTicketButton;
    private JButton deleteTicketButton;
    private JComboBox cBoxPriority;
    private JList<Ticket> ticketList;
    private JPanel rootPanel;
    private JButton saveQuitButton;

    DefaultListModel<Ticket> ticketListModel;
    ArrayList<Ticket> resolvedTickets = new ArrayList<Ticket>(); //new arraylist to store resolved tickets and later put to txt file

    public TicketGUI() {
        super("List of Support Tickets");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(400, 500));

        final String one = "1";
        final String two = "2";
        final String three = "3";
        final String four = "4";
        final String five = "5";
        cBoxPriority.addItem(one);
        cBoxPriority.addItem(two);
        cBoxPriority.addItem(three);
        cBoxPriority.addItem(four);
        cBoxPriority.addItem(five);

        ticketListModel = new DefaultListModel<Ticket>();
        ticketList.setModel(ticketListModel);
        ticketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        addTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String issue = txtIssue.getText();
                String priority = (String)cBoxPriority.getSelectedItem();
                String reporter = txtReportedBy.getText();
                Date dateReported = new Date(); //Default constructor creates date with current date/time
                int priorityAsInt = Integer.parseInt(priority);
                Ticket newTicket = new Ticket(issue, priorityAsInt, reporter, dateReported);
                TicketGUI.this.ticketListModel.addElement(newTicket);
            }
        });
        deleteTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date dateResolved = new Date(); //Default constructor creates date with current date/time
                Ticket toDelete = TicketGUI.this.ticketList.getSelectedValue();
                txtResolved.setText("");
                txtResolved.setEditable(true);
                try {
                    toDelete.setResolution(txtResolved.getText());
                    toDelete.setResolvedDate(dateResolved);
                    resolvedTickets.add(toDelete);

                }
                catch (NullPointerException npe) {
                    JOptionPane.showMessageDialog(TicketGUI.this, "Enter how you resolved the issue");
                    return;
                }

            }
        });
        //If Ticket selected in list fill textboxes
        ticketList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //if Tickets are deleted.
                try {
                    txtReportedBy.setText(TicketGUI.this.ticketList.getSelectedValue().getReporter());
                    txtIssue.setText(TicketGUI.this.ticketList.getSelectedValue().getDescription());
                    txtReportedOn.setText(TicketGUI.this.ticketList.getSelectedValue().getDateReported().toString());
                    txtID.setText(TicketGUI.this.ticketList.getSelectedValue().getTicketID() + ""); //little hack to trick it into taking the int as String
                    try {
                        txtResolved.setText(TicketGUI.this.ticketList.getSelectedValue().getResolution());
                        txtResolvedOn.setText(TicketGUI.this.ticketList.getSelectedValue().getResolvedDate().toString());
                    } catch (NullPointerException nfe) { //if the issue is not resolved yet
                        txtResolved.setText("Unresolved");
                        txtResolvedOn.setText("Unresolved");
                    }
                } catch (NullPointerException nfe2) {
                    return;
                }


            }
        });
        //if issue or reporter are changed remove all other entrees
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearTxt();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearTxt();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            //don't need it but needs to be declared
            }
        };
        txtIssue.getDocument().addDocumentListener(docListener);
        txtReportedBy.getDocument().addDocumentListener(docListener);

        //save all resolved and unresolved tickets to files
        saveQuitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Open a new buffered writer to write the resolved files

            }
        });
    }
    //Method to clear the textboxes
    public void clearTxt() {
        txtReportedOn.setText("");
        txtID.setText(""); //little hack to trick it into taking the int as String
        txtResolved.setText("");
        txtResolvedOn.setText("");
    }
}
