/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util.compatibility;

import java.util.StringTokenizer;

/**
 * Class to get current JRE version.
 * @author ivan
 */
public final class JREVersion implements Comparable
{
  private int major;
  private int minor;

  public final static JREVersion JRE_1_3 = new JREVersion(1,3);
  public final static JREVersion JRE_1_4 = new JREVersion(1,4);
  public final static JREVersion JRE_1_5 = new JREVersion(1,5);
  public final static JREVersion JRE_1_6 = new JREVersion(1,6);
  
  public static JREVersion getCurrentVersion()
  {
    StringTokenizer version = new StringTokenizer( System.getProperty("java.version"), "." );
    int major=0, minor=0;
    
    if( version.hasMoreElements() )
    {
      major = Integer.parseInt( version.nextToken() );
    }

    if( version.hasMoreElements() )
    {
      minor = Integer.parseInt( version.nextToken() );
    }

    return new JREVersion( major, minor );
  }
  
  public JREVersion( int major, int minor )
  {
    this.major = major;
    this.minor = minor;
  }

  public int getMajor()
  {
    return major;
  }

  public int getMinor()
  {
    return minor;
  }

  public boolean equals(Object obj)
  {
    JREVersion that = (JREVersion)obj;
    return (this.getMajor()==that.getMajor()) &&
           (this.getMinor()==that.getMinor());
  }

  public int compareTo(Object obj)
  {
    JREVersion that = (JREVersion)obj;
    
    if( this.getMajor()>that.getMajor() )
    {
      return 1;
    }
    else if( this.getMajor()<that.getMajor() )
    {
      return -1;
    }
    else
    {
      return this.getMinor() - that.getMinor();
    }
  }
  
  public boolean lowerThan( JREVersion that )
  {
    return this.compareTo(that)<0;
  }

  public boolean lowerThanOrEquals( JREVersion that )
  {
    return this.compareTo(that)<=0;
  }

  public boolean greaterThan( JREVersion that )
  {
    return this.compareTo(that)>0;
  }

  public boolean greaterThanOrEquals( JREVersion that )
  {
    return this.compareTo(that)>=0;
  }
}
