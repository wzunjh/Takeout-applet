package com.njh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.njh.dao.AddressBookMapper;
import com.njh.domain.AddressBook;
import com.njh.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {


}
