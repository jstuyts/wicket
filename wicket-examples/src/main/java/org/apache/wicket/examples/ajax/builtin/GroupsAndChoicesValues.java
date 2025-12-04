/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.examples.ajax.builtin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupsAndChoicesValues implements Serializable
{
    private List<Integer> checked = new ArrayList<>();
    private Integer radioed;
    private List<Integer> checkBoxMultiple = new ArrayList<>();
    private Integer radioChoice;

    public List<Integer> getChecked()
    {
        return checked;
    }

    public void setChecked(List<Integer> checked)
    {
        this.checked = checked;
    }

    public Integer getRadioed()
    {
        return radioed;
    }

    public void setRadioed(Integer radioed)
    {
        this.radioed = radioed;
    }

    public List<Integer> getCheckBoxMultiple()
    {
        return checkBoxMultiple;
    }

    public void setCheckBoxMultiple(List<Integer> checkBoxMultiple)
    {
        this.checkBoxMultiple = checkBoxMultiple;
    }

    public Integer getRadioChoice()
    {
        return radioChoice;
    }

    public void setRadioChoice(Integer radioChoice)
    {
        this.radioChoice = radioChoice;
    }

    @Override
    public String toString()
    {
        return "GroupsAndChoicesValues{" +
                "checked=" + checked +
                ", radioed=" + radioed +
                ", checkBoxMultiple=" + checkBoxMultiple +
                ", radioChoice=" + radioChoice +
                '}';
    }
}
