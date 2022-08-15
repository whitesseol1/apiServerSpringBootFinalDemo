package com.example.demo.src.neighbor;

import com.example.demo.src.trade.tradeDao;
import com.example.demo.src.trade.tradeProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class neighborService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final neighborDao neighborDao;
    private final neighborProvider neighborProvider;
    private final JwtService jwtService;

    @Autowired
    public neighborService(neighborDao neighborDao, neighborProvider neighborProvider,  JwtService jwtService) {
        this.neighborProvider = neighborProvider;
        this.neighborDao = neighborDao;
        this.jwtService = jwtService;

    }
}
