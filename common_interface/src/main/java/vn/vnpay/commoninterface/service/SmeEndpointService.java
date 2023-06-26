package vn.vnpay.commoninterface.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.entity.SmeEndpoint;
import vn.vnpay.dbinterface.repository.SmeEndpointRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmeEndpointService {

    @Autowired
    private SmeEndpointRepository smeEndpointRepository;

    public List<SmeEndpoint> getAllValidEndpoints() {
        List<SmeEndpoint> listEndpoints = smeEndpointRepository.findByStatus("1");
        if (listEndpoints == null) {
            return new ArrayList<>();
        }
        return listEndpoints;
    }

}
