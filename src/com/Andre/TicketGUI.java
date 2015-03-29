package com.Andre;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    ArrayList<Ticket> openTicketsList = new ArrayList<Ticket>(); //new arraylist to store open tickets and later put to txt file
    public TicketGUI() throws IOException{
        super("List of Support Tickets");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(1000, 500));

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

        try {
            BufferedReader openTickets = new BufferedReader(new FileReader("open_tickets.txt"));
            String line = openTickets.readLine();

            while (line != null) {
                String words[] = line.split("[,:]");
                line = openTickets.readLine();
                String desc = words[3];
                int prio = Integer.parseInt(words[5].trim());
                String rep = words[7];
                //create a string of the read out date
                //(could not parse it like this)
                String dateString = (words[9] + ":" + words[10] + ":" + words[11]).trim();
                //split the new date string
                String dateParse[] = dateString.split(" ");
                //give it a new format to fit the formatter
                dateString = dateParse[2] + " " + dateParse[1] + " " +
                        dateParse[5] + " " + dateParse[3];
                //create simple format to parse
                DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                //try to parse string into date
                try {
                    Date date = formatter.parse(dateString);
                    //once date is parses create new ticket and put into queue
                    Ticket queue = new Ticket(desc, prio, rep, date);
                    openTicketsList.add(queue);
                    TicketGUI.this.ticketListModel.addElement(queue);
                }
                //catch exception
                catch (ParseException ex){
                    ex.printStackTrace();
                }
            }
        }
        //ignore if it doesn't exist
        catch (FileNotFoundException e){

        }


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
                openTicketsList.add(newTicket);
                System.out.println(openTicketsList.toString());
            }
        });
        deleteTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if a ticket is selected to be resolved
                if (!TicketGUI.this.ticketList.isSelectionEmpty()) {
                    Ticket toDelete = TicketGUI.this.ticketList.getSelectedValue();

                    Date dateResolved = new Date(); //Default constructor creates date with current date/time

                    //open input dilog to resolve the ticket
                    String s = JOptionPane.showInputDialog(
                            null,
                            "How did you resolve the issue:",
                            "Resolve Ticket",
                            JOptionPane.PLAIN_MESSAGE);

                    //If a string was returned, set resolution and date, add to resolved list, and delete ticket from list
                    //if not ignore it
                    if ((s != null) && (s.length() > 0)) {
                        openTicketsList.remove(toDelete);
                        txtResolved.setText(s);
                        toDelete.setResolvedDate(dateResolved);
                        toDelete.setResolution(txtResolved.getText());
                        resolvedTickets.add(toDelete);
                        TicketGUI.this.ticketListModel.removeElement(toDelete);
                        clearTxt();
                        txtIssue.setText("");
                        txtReportedBy.setText("");
                        return;
                    }
                }
                //show error message if no ticket is selected
                else {
                    JOptionPane.showMessageDialog(null,
                            "Please select a ticket to be resolved!",
                            "Error", JOptionPane.ERROR_MESSAGE);
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
                DateFormat df = new SimpleDateFormat("MMM_dd_yyyy");
                // Get the date today using Date object.
                Date today = new Date();
                // Using DateFormat format method we can create a string
                // representation of a date with the defined format
                // to use to create todays resolve file.
                String reportDate = df.format(today);

                //Open a new buffered writers to write the resolved and open tickets to files
                try {
                    BufferedWriter todayResolved = new BufferedWriter(new FileWriter("Resolved tickets_as_of_" +
                            reportDate + ".txt"));
                    BufferedWriter open = new BufferedWriter(new FileWriter("open_tickets.txt"));
                    for (Ticket openTicket : openTicketsList) {
                        open.write(openTicket.toString() + "\n");
                    }
                    for (Ticket resolved : resolvedTickets) {
                        todayResolved.write(resolved.toString() + "\n");
                    }

                    todayResolved.close();
                    open.close();
                }
                catch (IOException iex) {
                    System.out.println(iex);

                }
                System.exit(0);


            }
        });
    }
    //Method to clear the textboxes
    public void clearTxt() {
        txtReportedOn.setText("");
        txtID.setText("");
        txtResolved.setText("");
        txtResolvedOn.setText("");
    }
}
