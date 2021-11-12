// Etaus.java - Extended taus random number generator  Version 0.1.0
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

// This java class is for the Tausworthe random number generator
// by Pierre L'Ecuyer with the addition of the Bays-Durham shuffle.
// See the C implementation of etaus at:
// https://github.com/aquila57
// This implementation of etaus uses 32-bit integers for calculating
// the next generation of random numbers.
// Since all results are in Java integers, all calculations
// should be based on numbers no larger than 2^31.
// For example the limit in genint() should be =< 2^31.
// For example the power in genpwr() should be =< 31.
// Since these methods are called billions of times,
// input parameters are not validated. 

public class Etaus
   {

	// number of states in state array
	// the state array is used in the Bays-Durham shuffle
   private static int states = 16384;
   private static int ofst;         // offset into state array
	private static int ofstmsk = 0x00003fff;
	private static int intmsk  = 0xffffffff;
	private static int msk0    = 0xffffffff;
	private static int msk1    = 0xfffffffe;
	private static int msk2    = 0xfffffff8;
	private static int msk3    = 0xfffffff0;
	private static int halfmax = 0x7fffffff;
	private static int s1;          // taus s1
	private static int s2;          // taus s2
	private static int s3;          // taus s3
	private static int out;         // current output state
   private static int prev;        // prev output state
   private static int pprev;       // prev prev output state
   // state array of 1024 members
   private static int state[] = new int[states];

	Etaus()
	   {
		// these three seeds are overridden in the
		// strt method
		s1 = 123456789;
		s2 = s1 + 17;
		s3 = s1 + 31;
		} // constructor

   // There are three parts to the Tausworthe algorithm

   private static void one()
	   {
		int lft;
		int rgt;
		lft = (s1 & msk1) << 12; 
		rgt = ((s1<<13) ^ s1) >>> 19;
		s1  = lft ^ rgt;
		} // one

   private static void two()
	   {
		int lft;
		int rgt;
		lft = (s2 & msk2) << 4;
		rgt = ((s2 << 2) ^ s2) >>> 25;
		s2  = (lft ^ rgt);
		} // two

   private static void tre()
	   {
		int lft;
		int rgt;
		lft = (s3 & msk3) << 17;
		rgt = ((s3 << 3) ^ s3) >>> 11;
		s3  = (lft ^ rgt);
		} // tre

   // smplgen is used by init and strt to
	// populate the state array

   private static int smplgen()
	   {
		one();
		two();
		tre();
		out = s1 ^ s2 ^ s3;
		return(out);
		} // smplgen

   public static int gen()
	   {
		int tmp;
		pprev = prev;
		prev  = out;
		one();
		two();
		tre();
		out = (s1 ^ s2 ^ s3);
		//****************************************************
		// Bays-Durham shuffle
		// The offset into the state array is 14 bits
		// There are 16384 (2^14) elements in the state array
		//****************************************************
		ofst = pprev >>> 18;
		tmp = out;
		out = state[ofst];
		state[ofst] = tmp;
		return(out);
		} // gen

//***********************************************************
// C version of the Tausworthe algorithm, shown as
// comments
//***********************************************************
// #define TAUSONE (et->s1 = (((et->s1&0xfffffffe)<<12) \
      // ^(((et->s1<<13)^et->s1)>>19)))

// #define TAUSTWO (et->s2 = (((et->s2&0xfffffff8)<< 4) \
      // ^(((et->s2<< 2)^et->s2)>>25)))

// #define TAUSTRE (et->s3 = (((et->s3&0xfffffff0)<<17) \
      // ^(((et->s3<< 3)^et->s3)>>11)))

// #define TAUS (TAUSONE ^ TAUSTWO ^ TAUSTRE)
//***********************************************************

// Generate a uniform random number from zero to one

	public static double genunif()
	   {
		int i;
		double frac;
		double maxint;
		maxint = 65536.0 * 32768.0;
		i = gen();
		frac = (double) i;
		if (frac < 0.0) frac = -frac;
		frac = frac / maxint;
		return(frac);
		} // genunif

   // genfrac returns a 53 bit fraction
	// This routine is slower than genunif,
	// but more accurate

	public static double genfrac()
	   {
		int i;
		double frac;
		frac = 0.0;
		for (i=0;i<53;i++)
		   {
		   int j;
			j = gen();
			if (j > 0)
			   {
			   frac = frac * 0.5;
				frac = frac + 0.5;
				} // if greater than zero
			else if (j < 0)
			   {
			   frac = frac * 0.5;
				} // else if less than zero
			} // for each bit in mantissa
		return(frac);
		} // genfrac

   // generate a uniform random integer from zero
	// to limit minus one

	public static int genint(int lmt)
	   {
		int i;
		double dbllmt;
		double frac;
		dbllmt = (double) lmt;
		frac = genunif();
		frac = frac * dbllmt;
		i = (int) frac;
		return(i);
		} // genint

	// generate a random number of pwr bits
	// example: genpwr(4) generates a 4-bit
	// uniform random number from zero to fifteen

	public static int genpwr(int pwr)
	   {
		int i;
		int shft;
		shft = 32 - pwr;
		i = gen();
		i = i >>> shft;
		return(i);
		} // genpwr

   // generate a 32-bit integer with a uniform
	// value of either zero or one

	public static int genbit()
	   {
		double frac;
		frac = genunif();
		if (frac >= 0.5)
		   {
			return(1);
			} // if a one bit
		else
		   {
			return(0);
			} // else a zero bit
		} // genbit

   // display the etaus state minus the state array

	public static void dspl()
	   {
		System.out.print("32 bit msk ");
		System.out.println(Integer.toHexString(msk0));
		System.out.print("s1 msk ");
		System.out.println(Integer.toHexString(msk1));
		System.out.print("s2 msk ");
		System.out.println(Integer.toHexString(msk2));
		System.out.print("s3 msk ");
		System.out.println(Integer.toHexString(msk3));
		System.out.print("s1 ");
		System.out.println(Integer.toHexString(s1));
		System.out.print("s2 ");
		System.out.println(Integer.toHexString(s2));
		System.out.print("s3 ");
		System.out.println(Integer.toHexString(s3));
		System.out.print("out   ");
		System.out.println(Integer.toHexString(out));
		System.out.print("prev  ");
		System.out.println(Integer.toHexString(prev));
		System.out.print("pprev ");
		System.out.println(Integer.toHexString(pprev));
		System.out.print("ofst  ");
		System.out.println(Integer.toHexString(ofst));
		} // dspl

	// display the state array

	public static void dsplState()
	   {
		int i;
		for (i=0;i<states;i++)
		   {
			System.out.print(i + ". ");
			System.out.println(Integer.toHexString(state[i]));
			} // for each state in state array
		} // dsplState

   // initialize the etaus structure with
	// three input parameters for s1,s2,s3
	// respectively
	// the input parameter is an array of three
	// seeds of type long

   public static void strt(int seed[])
	   {
		int i;
		s1 = seed[0];
		s2 = seed[1];
		s3 = seed[2];
		// warm up the seeds
		for (i=0;i<128;i++)
		   {
			int tmp;
			tmp = smplgen();
			} // for each warmup iteration
		out   = smplgen();
		prev  = smplgen();
		pprev = smplgen();
		// populate the state array with random
		// data
		for (i=0;i<states;i++)
		   {
			int tmp;
			tmp = smplgen();
			state[i] = tmp;
			} // for each warmup iteration
		} // strt

   public static void aboutEtaus()
	   {
      System.out.print("Etaus random number generator  ");
      System.out.println("Integer Java Version 0.1.0");
      System.out.print("Copyright (C) 2021 ");
      System.out.println("aquila57 at github.com");
      System.out.println();
      System.out.print("This program is free software; ");
      System.out.println("you can redistribute it and/or modify");
      System.out.print("it under the terms of the GNU General ");
      System.out.println("Public License as published by");
      System.out.print("the Free Software Foundation; either ");
      System.out.println("version 2 of the License, or");
      System.out.println("(at your option) any later version.");
      System.out.println();
      System.out.print("This program is distributed in the ");
      System.out.println("hope that it will be useful,");
      System.out.print("but WITHOUT ANY WARRANTY; without ");
      System.out.println("even the implied warranty of");
      System.out.print("MERCHANTABILITY or FITNESS FOR A ");
      System.out.println("PARTICULAR PURPOSE.  See the");
      System.out.print("GNU General Public License ");
      System.out.println("for more details.");
      System.out.println();
      System.out.print("You should have received a copy ");
      System.out.println("of the GNU General Public License along");
      System.out.print("with this program; if not, write ");
      System.out.println("to the Free Software Foundation, Inc.,");
      System.out.print("51 Franklin Street, Fifth Floor, ");
      System.out.println("Boston, MA 02110-1301 USA.");
      System.out.println();
		} // aboutEtaus

   } // class Etaus
