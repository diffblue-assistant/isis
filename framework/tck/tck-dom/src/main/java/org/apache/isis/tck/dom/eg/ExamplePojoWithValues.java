/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.tck.dom.eg;

import java.util.Date;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;

@ObjectType("EPV")
public class ExamplePojoWithValues extends ExamplePojo {
    
    
    public String title() {
        return getName();
    }


    // {{ Name
    private String name;

    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Date (property)
    private Date date;

    @Optional
    @MemberOrder(sequence = "1")
    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }
    // }}

    // {{ Size
    private int size;

    @MemberOrder(sequence = "1")
    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    // }}

    // {{ Nullable
    private Long number;

    @Optional
    @MemberOrder(sequence = "1")
    public Long getNullable() {
        return number;
    }

    public void setNullable(final Long number) {
        this.number = number;
    }

    // }}

    // {{ NotPersisted
    @NotPersisted
    public int getNotPersisted() {
        throw new org.apache.isis.applib.ApplicationException("unexpected call");
    }
    // }}


}
