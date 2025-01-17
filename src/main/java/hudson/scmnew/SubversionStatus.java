/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
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
package hudson.scmnew;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;

import java.util.regex.Pattern;
import java.util.UUID;

/**
 * Receives the push notification of commits from repository.
 * Opened up for untrusted access.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class SubversionStatus implements UnprotectedRootAction {
    public String getDisplayName() {
        return "Subversion";
    }

    public String getIconFileName() {
        // TODO
        return null;
    }

    public String getUrlName() {
        return "subversion";
    }

    public SubversionRepositoryStatus getDynamic(String uuid) {
        if(UUID_PATTERN.matcher(uuid).matches())
            return new SubversionRepositoryStatus(UUID.fromString(uuid));
        return null;
    }

    private static final Pattern UUID_PATTERN = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");
}
