package com.assignment.core.services;

import java.util.List;

public interface SlackChannelService {
    List<String> getChannels(String accessToken);
}