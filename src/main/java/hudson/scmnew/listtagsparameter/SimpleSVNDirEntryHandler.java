/*
 * The MIT License
 *
 * Copyright (c) 2010-2012, Manufacture Francaise des Pneumatiques Michelin,
 * Romain Seguy, id:kutzi, id:grahamparks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hudson.scmnew.listtagsparameter;

import hudson.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

/**
 * Simple {@link ISVNDirEntryHandler} used to get a list containing all the
 * directories in a given Subversion repository.
 *
 * @author Romain Seguy (http://openromain.blogspot.com)
 */
public class SimpleSVNDirEntryHandler implements ISVNDirEntryHandler {

  private final List<SVNDirEntry> dirs = new ArrayList<>();
  private final Pattern filterPattern;

  public SimpleSVNDirEntryHandler(String filter) {
    if(Util.fixEmpty(filter) != null) {
      filterPattern = Pattern.compile(filter);
    } else {
      filterPattern = null;
    }
  }

  public List<String> getDirs() {
    return getDirs(false, false);
  }

  public @NonNull List<String> getDirs(boolean reverseByDate, boolean reverseByName) {
    if (reverseByDate) {
      dirs.sort(Comparator.comparing(SVNDirEntry::getDate).reversed());
    } else if(reverseByName) {
      dirs.sort(Comparator.comparing(SVNDirEntry::getName).reversed());
    } else {
      dirs.sort(Comparator.comparing(SVNDirEntry::getName));
    }

    List<String> sortedDirs = new ArrayList<>();
    for (SVNDirEntry dirEntry : dirs) {
      sortedDirs.add(dirEntry.getName());
    }
 
    return sortedDirs;
  }

  @Override
  public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
    if(filterPattern == null || filterPattern.matcher(dirEntry.getName()).matches()) {
      dirs.add(dirEntry);
    }
  }

}
