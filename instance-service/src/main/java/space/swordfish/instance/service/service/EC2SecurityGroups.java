package space.swordfish.instance.service.service;


import space.swordfish.instance.service.domain.SecurityGroup;

import java.util.List;

public interface EC2SecurityGroups {

    List<SecurityGroup> getAll();

}
