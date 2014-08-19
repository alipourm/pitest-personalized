/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.pitest.mutationtest.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.pitest.classinfo.ClassName;

public class MutationIdentifier implements Comparable<MutationIdentifier> {

  private final Location      location;
  private final List<Integer> indexes;
  private final String        mutator;

  public MutationIdentifier(final Location location, final int index,
      final String mutatorUniqueId) {
    this(location, Collections.singleton(index), mutatorUniqueId);
  }

  public MutationIdentifier(final Location location,
      final Collection<Integer> indexes, final String mutatorUniqueId) {
    this.location = location;
    this.indexes = new ArrayList<Integer>(indexes);
    this.mutator = mutatorUniqueId;

  }
  
  // AMIN DIRTY HACK
  public MutationIdentifier clone(int i){
	  MutationIdentifier s = new MutationIdentifier(this.location, this.indexes, this.mutator);
	
	  return s;
  }
  

  public Location getLocation() {
    return this.location;
  }

  public String getMutator() {
    return this.mutator;
  }

  public int getFirstIndex() {
    return this.indexes.iterator().next();
  }

  public MutationIdentifier withLocation(final Location location) {
    return new MutationIdentifier(location, this.indexes, this.mutator);
  }

  public MutationIdentifier withMutator(final String mutator) {
    return new MutationIdentifier(this.location, this.indexes, mutator);
  }

  public MutationIdentifier withIndex(final int id) {
    return new MutationIdentifier(this.location, id, this.mutator);
  }



  
  @Override
  public String toString() {
    return "MutationIdentifier [location=" + this.location + ", indexes="
        + this.indexes + ", mutator=" + this.mutator + "]";
  }

  
  public JSONObject toJSON(){
	  JSONObject js = new JSONObject();
	  js.put("location", this.location.toJSON());
	  js.put("indexes", this.indexes.toString());
	  js.put("mutator", this.mutator);
	  return js;
  }
  
  
    public boolean matches(final MutationIdentifier newId) {
    return this.location.equals(newId.location)
        && this.mutator.equals(newId.mutator)
        && this.indexes.contains(newId.getFirstIndex());
  }

  
  public ClassName getClassName() {
    return this.location.getClassName();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result)
        + ((this.indexes == null) ? 0 : this.indexes.hashCode());
    result = (prime * result)
        + ((this.location == null) ? 0 : this.location.hashCode());
    result = (prime * result)
        + ((this.mutator == null) ? 0 : this.mutator.hashCode());
  
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MutationIdentifier other = (MutationIdentifier) obj;
    if (this.indexes == null) {
      if (other.indexes != null) {
        return false;
      }
    } else if (!this.indexes.equals(other.indexes)) {
      return false;
    }
    if (this.location == null) {
      if (other.location != null) {
        return false;
      }
    } else if (!this.location.equals(other.location)) {
      return false;
    }
    if (this.mutator == null) {
      if (other.mutator != null) {
        return false;
      }
    } else if (!this.mutator.equals(other.mutator)) {
      return false;
    } 
    return true;
  }

  public int compareTo(final MutationIdentifier other) {
    int comp = this.location.compareTo(other.getLocation());
    if (comp != 0) {
      return comp;
    }
    comp = this.mutator.compareTo(other.getMutator());
    if (comp != 0) {
      return comp;
    }
    return this.indexes.get(0).compareTo(other.indexes.get(0));
  }

}
