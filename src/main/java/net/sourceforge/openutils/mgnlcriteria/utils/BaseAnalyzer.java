/**
 *
 * Criteria API for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlcriteria.html)
 * Copyright(C) 2009-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.openutils.mgnlcriteria.utils;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;


/**
 * Base analyzer class. Subclasses should simply implement the tokenFiltersChain() mathod in order to add TokenFilters.
 * @author fgiust
 * @version $Id$
 */
public abstract class BaseAnalyzer extends Analyzer
{

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        StandardTokenizer tokenStream = tokenize(reader);
        return tokenFiltersChain(tokenStream);
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams();
            setPreviousTokenStream(streams);
            streams.tokenStream = tokenize(reader);
            streams.filteredTokenStream = tokenFiltersChain(streams.tokenStream);
        }
        else
        {
            streams.tokenStream.reset(reader);
        }

        return streams.filteredTokenStream;
    }

    /**
     * Tokenize using a StandardTokenizer. Subclasses may override this mehod.
     * @param reader base reader
     * @return tokenizer
     */
    protected StandardTokenizer tokenize(Reader reader)
    {
        return new StandardTokenizer(Version.LUCENE_30, reader);
    }

    /**
     * Apply a set of TokenFilters to the TokenStream
     * @param tokenStream original tokenStream
     * @return filtered tokenStream
     */
    protected abstract TokenStream tokenFiltersChain(TokenStream tokenStream);

    private static final class SavedStreams
    {

        StandardTokenizer tokenStream;

        TokenStream filteredTokenStream;
    }

}
