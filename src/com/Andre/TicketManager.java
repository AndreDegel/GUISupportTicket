package com.Andre;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/*
Program to Manage Support Tickets
 */
public class TicketManager {

    public static void main(String[] args) throws IOException{
        TicketGUI gui = new TicketGUI();
/*
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("MMM_dd_yyyy");
        // Get the date today using Date object.
        Date today = new Date();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format
        // to use to create todays resolve file.
        String reportDate = df.format(today);
        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();
        LinkedList<Ticket> resolvedTicket = new  LinkedList<Ticket>();
        //try to open the file if exists
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
                    ticketQueue.add(queue);
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

        Scanner scan = new Scanner(System.in);

        while(true){

            System.out.println("1. Enter Ticket\n2. Delete by ID\n3. Delete by Issue\n4. Search by Name\n5. Display All Tickets\n6. Quit");
            int task = Integer.parseInt(scan.nextLine());

            if (task == 1) {
                //Call addTickets, which will let us enter any number of new tickets
                addTickets(ticketQueue);

            } else if (task == 2) {
                //delete a ticket by its ID
                deleteByID(ticketQueue, resolvedTicket);

            } else if (task == 3) {
                //delete a ticket by its Issue
                deleteByIssue(ticketQueue, resolvedTicket);


            } else if (task == 4){
                //search a ticket by the one who reported it
                searchByName(ticketQueue);

            } else if (task == 6 ) {
                //Quit. Future prototype may want to save all tickets to a file
                BufferedWriter open = new BufferedWriter(new FileWriter("open_tickets.txt"));
                BufferedWriter todayResolved = new BufferedWriter(new FileWriter("Resolve tickets_as_of_" +
                        reportDate + ".txt"));
                for (Ticket openTicket : ticketQueue) {
                    open.write(openTicket.toString() + "\n");
                }
                for (Ticket resolved : resolvedTicket){
                    todayResolved.write(resolved.toString() + "\n");
                }
                open.close();
                todayResolved.close();

                System.out.println("Quitting program");
                break;
            }
            else {
                //this will happen for 3 or any other selection that is a valid int
                //TODO Program crashes if you enter anything else - please fix
                //Default will be print all tickets
                printAllTickets(ticketQueue);
            }
        }

        scan.close();

    }


    protected static void deleteByID(LinkedList<Ticket> ticketQueue,LinkedList<Ticket> resolved) {
        printAllTickets(ticketQueue);   //display list for user
        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }
        int deleteID;
        boolean found = false;
        Scanner deleteScanner = new Scanner(System.in);
        //validate the try of user input for ticket ID
        do {
            System.out.println("Enter ID of ticket to delete");
            while (!deleteScanner.hasNextInt()) {
                //prompt error if its not a number
                System.out.println("That's not a number! Try again.");
                deleteScanner.next();
            }
            deleteID = deleteScanner.nextInt();
            //Loop over all tickets. Delete the one with this ticket ID
            for (Ticket ticket : ticketQueue) {
                //if user input is valid and Id is in list
                //remove ticket and break out of the loop
                if (ticket.getTicketID() == deleteID) {
                    found = true;
                    ticketQueue.remove(ticket);
                    System.out.println("Enter resolution for " + ticket.getDescription());
                    String resolution = deleteScanner.next();
                    ticket.setResolution(resolution);
                    ticket.setResolvedDate(new Date());
                    resolved.add(ticket);
                    System.out.println(String.format("Ticket %d deleted", deleteID));

                    break; //don't need loop any more.
                }
            }
            //prompt if negative number and not found
            if (!found) {
                System.out.println("Ticket ID not found! No negative numbers");
            }
        }
        //if not reloop and give another try
        while (!found);
        printAllTickets(ticketQueue);  //print updated list
        }


    protected static void deleteByIssue(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolved) {

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        String deleteIssue;
        boolean found = false;  //set finding bool to false
        Scanner deleteScanner = new Scanner(System.in);
        LinkedList<Ticket> byIssue = new LinkedList<Ticket>();   //initialize new list to be filled with found issues
        do {
            //search input from user
            System.out.println("Enter an issue to search.");
            deleteIssue = deleteScanner.next().toLowerCase();
            //check search criteria in the ticket list
            for (Ticket ticket : ticketQueue) {
                //if the issue is in the list add to search list
                //and set found bool to true
                if (ticket.getDescription().toLowerCase().contains(deleteIssue)) {
                    byIssue.add(ticket);
                    found = true;
                }
            }
            //if issue is not in the list print an error
            if (!found) {
                System.out.println("Issue not found! Try again");
            }
        }
        //test user input and rerun until issue is in list
        while (!found);
        //prompt result of search and ask if delete is wanted
        printAllTickets(byIssue);
        String answer;
        int deleteID;
        System.out.println("Do you want to delete any of the tickets in the search list? [y/n]");
        answer = deleteScanner.next();
        if (answer.equalsIgnoreCase("y")) {
            found = false;
            do {
                System.out.println("Enter ID of ticket to delete");
                while (!deleteScanner.hasNextInt()) {
                    //prompt error if its not a number
                    System.out.println("That's not a number! Try again.");
                    deleteScanner.next();
                }
                deleteID = deleteScanner.nextInt();
                //Loop over all tickets. Delete the one with this ticket ID
                for (Ticket ticket : byIssue) {
                    //if user input is valid and Id is in list
                    //remove ticket and break out of the loop
                    if (ticket.getTicketID() == deleteID) {
                        found = true;
                        ticketQueue.remove(ticket);
                        System.out.println("Enter resolution for " + ticket.getDescription());
                        String resolution = deleteScanner.next();
                        ticket.setResolution(resolution);
                        ticket.setResolvedDate(new Date());
                        resolved.add(ticket);
                        System.out.println(String.format("Ticket %d deleted", deleteID));
                        break; //don't need loop any more.
                    }
                }
                //prompt if negative number and not found
                if (!found) {
                    System.out.println("Ticket ID not found! No negative numbers");
                }


            }
            //if not reloop and give another try
            while (!found);

        }
    }


    protected static void searchByName(LinkedList<Ticket> ticketQueue) {

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        String searchName;      //initialize name to be searched
        boolean found = false;  //set finding bool to false
        LinkedList<Ticket> byName = new LinkedList<Ticket>();   //initialize new list to be filled with found names
        Scanner searchScanner = new Scanner(System.in);
            do {
                //search input from user
                System.out.println("Enter a name to search.");
                searchName = searchScanner.next();
                //check search criteria in the ticket list
                for (Ticket ticket : ticketQueue) {
                    //if the name is in the list add to search list
                    //and set found bool to true
                    if (ticket.getReporter().contains(searchName)) {
                        byName.add(ticket);
                        found = true;
                    }
                }
                //if name is not in the list print an error
                if (!found) {
                    System.out.println("Name not found! Try again");
                }
            }
            //test user input and rerun until name is in list
            while (!found);

        printAllTickets(byName);
        }





    protected static void addTickets(LinkedList<Ticket> ticketQueue) {
        Scanner sc = new Scanner(System.in);
        boolean moreProblems = true;
        String description, reporter;
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        while (moreProblems){
            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            priority = Integer.parseInt(sc.nextLine());

            Ticket t = new Ticket(description, priority, reporter, dateReported);
            //ticketQueue.add(t);
            addTicketInPriorityOrder(ticketQueue, t);

            printAllTickets(ticketQueue);

            System.out.println("More tickets to add?");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }
    }

    protected static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket){

        //Logic: assume the list is either empty or sorted

        if (tickets.size() == 0 ) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < tickets.size() ; x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }

    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");

        for (Ticket t : tickets ) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on its argument
        }
        System.out.println(" ------- End of ticket list ----------");
*/
    }
}