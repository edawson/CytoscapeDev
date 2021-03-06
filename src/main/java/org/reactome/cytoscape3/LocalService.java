/*
 * Created on Jul 20, 2010
 *
 */
package org.reactome.cytoscape3;

import java.io.IOException;
import java.util.Set;

import org.reactome.r3.graph.NetworkBuilderForGeneSet;
import org.reactome.r3.util.InteractionUtilities;

/**
 * This singleton class is used to create some local services which reduce the
 * burden on the server to improve the performance. The use of this class is
 * bound to PlugInScoreObjectManager, which is used to control dynamic loading
 * of this class and ensure only a single instance of LocalService is present.
 * Note: Construction of an FI network with linker genes only occurs locally in
 * its current implementation, as doing so using the server takes too long.
 * 
 * @author wgm ported July 2013 by Eric T Dawson
 */
public class LocalService implements FINetworkService
{
    // Cache some fetched information for performance reasons.
    // Don't cache anything in case the FI network version has
    // changed during a previous session.
    private Set<String> allFIs;

    public LocalService()
    {
    }

    @Override
    public Integer getNetworkBuildSizeCutoff() throws Exception
    {
        // There is no limit to network size yet.
        return Integer.MAX_VALUE;
    }

    @Override
    public Set<String> queryAllFIs() throws IOException
    {
        RESTFulFIService restService = new RESTFulFIService();
        Set<String> allFIs = restService.queryAllFIs();
        return allFIs;
    }

    @Override
    public Set<String> buildFINetwork(Set<String> selectedGenes,
            boolean useLinker) throws Exception
    {
        Set<String> fis;
        Set<String> allFIs = queryAllFIs();
        if (useLinker)
        {
            NetworkBuilderForGeneSet networkBuilder = new NetworkBuilderForGeneSet();
            networkBuilder.setAllFIs(allFIs);
            fis = networkBuilder.constructFINetworkForGeneSet(selectedGenes,
                    null);
        }
        else
        {
            fis = InteractionUtilities.getFIs(selectedGenes, allFIs);
        }
        return fis;
    }
}
