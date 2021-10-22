package com.bradyrussell.flow.lib.repository;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.repository.rest.RestFlowRepository;

import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

public class RepositoryProtocolRegistry {
    public static final HashMap<String, Function<String, FlowRepository>> protocols = new HashMap<>();

    static {
        protocols.put("rest", RestFlowRepository::new);
    }

    public static FlowRepository get(String protocolAndAddress) {
        String[] parts = protocolAndAddress.split(Pattern.quote(Constants.RepositoryProtocolAddressDelimiter), 2);
        return protocols.get(parts[0]).apply(parts[1]);
    }

    public static FlowRepository get(String protocol, String address) {
        return protocols.get(protocol).apply(address);
    }

    public static void registerProtocol(String protocol, Function<String, FlowRepository> repositoryProvider) {
        protocols.put(protocol, repositoryProvider);
    }
}
