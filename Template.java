// Template.java - FIFO Queue Template Test  Version 0.1.0
// Copyright (C) 2021 aquila57 at github.com

// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to:

   // Free Software Foundation, Inc.
   // 59 Temple Place - Suite 330
   // Boston, MA 02111-1307, USA.

//**********************************************************
// This program uses an array of 1024 random integers,
// either zero or one, as a template for matching
// the equivalent output of the random number generator.
// The probability of not matching the first number in the
// template is 1/2.
// The probability of matching the first number in the
// template is 1/4.
// The probability of matching the first two numbers in
// the template is 1/8, etc.
// The probability of matching the entire array of
// 1024 integers is 1/(2^2048).
// This program tests one million sample queues against
// the template.
// For each sample, the actual queue is popped and pushed,
// before the queue is matched against the template.
// After one million samples have been made, a chi square
// test is performed on the numbers of matches found
// between the actual matches and the expected matches.
//**********************************************************

import java.lang.*;

public class Template
   {

   private int size      = 1024;   // size of template and queue
   private int sizePlus  = size + 1;  // total# of conditions
   private int samples   = 1000000;   // #samples in test
   private double dblSamples = (double) samples;
   private double prob   = 0.5;      // probability of no match
	// currProb[i+1] = currProb[i] * prob;
   private double currProb = prob;
	// number of sample queues actually matched
   private double actual[] = new double[sizePlus];
	// expected number of sample queues matched
   private double expected[] = new double[sizePlus];
	//************************************************************
	// The following are the head and tail nodes for
	// the template and the actual queue.
	//************************************************************
   private Node tmplHead = new Node();
   private Node tmplTail = new Node();
   private Node actHead  = new Node();
   private Node actTail  = new Node();
   private Etaus et = new Etaus();    // etaus class instance

	Template()
	   {
		int i;
		int prm;
		int parm[] = new int[8];
		double frac;
		//********************************************************
		// initialize actual array
		//********************************************************
		for (i=0;i<sizePlus;i++)
		   {
			actual[i] = 0.0;
			} // for each level of compare
		//********************************************************
		// Initialize etaus random number generator
		//********************************************************
		frac = Math.random();
		prm = (int) (frac * 2000000000.0);
		parm[0] = prm;
		System.out.print("Seed 1 ");
		System.out.println(Integer.toHexString(prm));
		frac = Math.random();
		prm = (int) (frac * 2000000000.0);
		parm[1] = prm;
		System.out.print("Seed 2 ");
		System.out.println(Integer.toHexString(prm));
		frac = Math.random();
		prm = (int) (frac * 2000000000.0);
		parm[2] = prm;
		System.out.print("Seed 3 ");
		System.out.println(Integer.toHexString(prm));
		Etaus et = new Etaus();
		et.strt(parm);
		//********************************************************
		// Initialize template and actual, head and tail
		//********************************************************
		tmplHead.prev = tmplTail;
		tmplTail.next = tmplHead;
		tmplHead.key  = 99999;
		tmplTail.key  = 11111;
		actHead.prev  = actTail;
		actTail.next  = actHead;
		actHead.key   = 88888;
		actTail.key   = 22222;
		//********************************************************
		// Calculate expected matches
		// currProb is initialized to prob.
		//********************************************************
		for (i=0;i<sizePlus;i++)
		   {
		   expected[i] = currProb * dblSamples;
			currProb *= prob;
			} // for each expected #matches
		} // constructor

   private class Node
	   {
		private Node next;
		private Node prev;
		private int key;
		} // class Node

	// add a new node to the head of the template queue

   public void pushTemplate(int key)
	   {
		// most recently added node is called last
		// x is the new node being added
		Node last = tmplHead.prev;
		Node x = new Node();
		x.next = tmplHead;
		x.prev = last;
		tmplHead.prev = x;
		last.next = x;
		x.key = key;
		} // pushTemplate

   // add a new node to the head of the sample queue
	
   public void pushActual(int key)
	   {
		// most recently added node is called last
		// x is the new node being added
		Node last = actHead.prev;
		Node x = new Node();
		x.next = actHead;
		x.prev = last;
		actHead.prev = x;
		last.next = x;
		x.key = key;
		} // pushActual

   // remove the least recently added node
	// in the template queue

   public void popTemplate()
	   {
		// the new, least recently added, node is
		// called the first node
		Node first = tmplTail.next;
		tmplTail.next = first.next;
		first.prev = tmplTail;
		} // popTemplate

   // remove the least recently added node
	// in the sample queue

   public void popActual()
	   {
		// the new, least recently added, node is
		// called the first node
		Node first = actTail.next;
		actTail.next = first.next;
		first.prev = actTail;
		} // popActual

   // Create a random template of zeros and ones.
	// The template is a doubly linked list.

   private void bldTemplate()
	   {
		int i;
		for (i=0;i<size;i++)
		   {
			int num;
			num = et.genbit();   // generate a zero or a one
			pushTemplate(num);   // add to end of linked list
			} // for each node in the template queue
		} // bldTemplate

   // for debugging, print the template queue

   private void showTemplate()
	   {
		int i;
		System.out.println("showTemplate");
		Node curr = new Node();
		curr = tmplTail.next;
		if (curr == tmplHead)
		   {
			System.out.print("showTemplate: ");
			System.out.println("empty queue");
			return;
			} // if curr == tmplHead
		i = 0;
		while (curr != tmplHead)
		   {
			i = i + 1;
			System.out.println(i + ". " + curr.key);
			curr = curr.next;
			} // for each node in template
		} // showTemplate

   // Create a random sample queue of zeros and ones.
	// The sample queue is a doubly linked list.

   private void bldActual()
	   {
		int i;
		for (i=0;i<size;i++)
		   {
			int num;
			num = et.genbit();   // generate a zero or a one
			pushActual(num);     // add to end of linked list
			} // for each node in the template queue
		} // bldActual

   // for debugging, print the sample queue

   private void showActual()
	   {
		int i;
		System.out.println("showActual");
		Node curr = new Node();
		curr = actTail.next;
		if (curr == actHead)
		   {
			System.out.print("showActual: ");
			System.out.println("empty queue");
			return;
			} // if curr == actHead
		i = 0;
		while (curr != actHead)
		   {
			i = i + 1;
			System.out.println(i + ". " + curr.key);
			curr = curr.next;
			} // for each node in template
		} // showActual

   // count the number of matches, left to right, of
	// the sample queue against the template queue
	// return the actual number of matches in the
	// oldest side of the queue
  	// If the number of matches, left to right,
  	// is 1024, then a wrap-around error is generated.

   public int match()
		{
		int tally;    // number of continuous matches left to right
		// the current position in each queue.
		Node currActual   = new Node();
		Node currTemplate = new Node();
		// match from tail to head
		currActual = actTail.next;
		if (currActual == actHead)
		   {
			System.out.print("match: ");
			System.out.println("empty actual queue");
			return(0);
			} // if currActual == actHead
		currTemplate = tmplTail.next;
		if (currTemplate == tmplHead)
		   {
			System.out.print("match: ");
			System.out.println("empty template queue");
			return(0);
			} // if currTemplate == tmplHead
		tally = 0;
		while (currActual != actHead)
		   {
			if (currTemplate == tmplHead)
			   {
				System.out.print("match: ");
				System.out.println("template node missing");
				return(0);
				} // if no corresponding template node
			// comparison ends at the firat non-match
			if (currActual.key != currTemplate.key)
			   {
				break;
				} // if node does not match
         tally = tally + 1;   // #nodes matched
			currActual   = currActual.next;
			currTemplate = currTemplate.next;
			} // for each actual node
  		if (tally >= size)
  		   {
  			System.out.print("match: ");
  			System.out.println("wrap-around error");
  			} // if wrap-around error
		return(tally);   // #matches left to right
	   } // match

   // generate one million rolling sample queues.
	// pop the oldest sample
	// push the newest sample
	// match the new queue
  	// return zero if no wrap-around error
  	// return one  if    wrap-around error

   public int takeSamples()
	   {
		int i;
		int num;
		int count = 0;
		int status;
		for (i=0;i<samples;i++)
		   {
			if (i == 500000)
			   {
				int parm[] = new int[8];
				parm[0] = 1;
				parm[1] = 2;
				parm[2] = 3;
				Etaus et = new Etaus();
				et.strt(parm);
				} // if half way taking samples
		   popActual();
			num = et.genbit();
		   pushActual(num);
		   count = match();
  			if (count >= size)
  			   {
  				System.out.print("Sample # ");
  				System.out.println(i + 1);
  				break;
  				} // if wrap-around error
		   actual[count] += 1.0;
			} // for each sample queue
  		status = 0;
      if (count >= size)
         {
         status = 1;
         } // if wrap-around error
  		return(status);
		} // takeSamples

   // Print the heading on the chi square report.
	// The first column is the # of matches

   public void printHeading()
	   {
		System.out.println();
		System.out.print("            ");
		System.out.println("         Template Test");
		System.out.print("            ");
		System.out.println("   etaus Random Number Generator");
		System.out.println();
		System.out.print("Matches  ");
		System.out.print("  Actual      ");
		System.out.print("  Expected  ");
		System.out.print("  Difference    ");
		System.out.println("Chi Square");
		} // printHeading

   public void calcChisq()
	   {
		int i;
		double df;          // degrees of freedom
		double chisq;       // chi square statistic
		double diff;        // actual - expected
		double diffsq;      // difference squared
		// initialize totals
		chisq = 0.0;
		df    = 0.0;
		// a valid chi square test had ten or more
		// expected tallies
		i = 0;
		while (expected[i] >= 10.0)
		   {
			diff = actual[i] - expected[i];
			diffsq = diff * diff;
			chisq = chisq + (diffsq / expected[i]);
			System.out.printf("%5d  ", i);
			System.out.printf("%10.0f  ", actual[i]);
			System.out.printf("%14.4f  ", expected[i]);
			System.out.printf("%12.4f  ", diff);
			System.out.printf("%10.4f\n", chisq);
			df = df + 1.0;
			i = i + 1;
			} // for each expected >= 10.0
		df = df - 1.0;
		System.out.println();
		System.out.printf("Chi square %10.4f\n", chisq);
		System.out.printf("Degrees of freedom %4.0f\n", df);
		} // calcChisq

	public static void main(String arg[])
	   {
		int status;
		Template tmpl = new Template();
		tmpl.bldTemplate();
		tmpl.bldActual();
  		status = tmpl.takeSamples();
  		if (status == 0)
  		   {
  		   tmpl.printHeading();
  		   tmpl.calcChisq();
  			} // if no wrap-around error
		} // main

	} // class Template
