package com.example.demo.src.trade;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class tradeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final tradeDao tradeDao;
    private final tradeProvider tradeProvider;
    private final JwtService jwtService;

    @Autowired
    public tradeService(tradeDao tradeDao, tradeProvider tradeProvider,  JwtService jwtService) {
        this.tradeProvider = tradeProvider;
        this.tradeDao = tradeDao;
        this.jwtService = jwtService;

    }


}
