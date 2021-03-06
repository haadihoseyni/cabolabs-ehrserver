/*
 * Copyright 2011-2017 CaboLabs Health Informatics
 *
 * The EHRServer was designed and developed by Pablo Pazos Gutierrez <pablo.pazos@cabolabs.com> at CaboLabs Health Informatics (www.cabolabs.com).
 *
 * You can't remove this notice from the source code, you can't remove the "Powered by CaboLabs" from the UI, you can't remove this notice from the window that appears then the "Powered by CaboLabs" link is clicked.
 *
 * Any modifications to the provided source code can be stated below this notice.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cabolabs.ehrserver.ehr.clinical_documents

class OperationalTemplateIndex {

   String templateId
   String concept          // Concept name of the OPT
   String language         // en formato ISO_639-1::en
   String uid
   String archetypeId      // root archetype id
   String archetypeConcept // concept name for the archetype root node
   
   String fileUid = java.util.UUID.randomUUID() as String
   
   // true => shared with all the organizations
   boolean isPublic
   
   Date dateCreated
   Date lastUpdated
   
   static hasMany = [referencedArchetypeNodes: ArchetypeIndexItem, 
                     templateNodes: OperationalTemplateIndexItem]
   
   static transients = ['lang']
   def getLang()
   {
      this.language.split('::')[1]
   }
   
   static namedQueries = {
      forOrg { org ->
         
         def shares = OperationalTemplateIndexShare.findAllByOrganization(org)
         
         if (shares)
         {
            or {
               eq('isPublic', true)
               'in'('id', shares.opt.id)
            }
         }
         else
         {
            eq('isPublic', true)
         }
      }
      
      likeConcept { concept ->
         if (concept)
         {
            like('concept', '%'+concept+'%')
         }
      }
   }
   
   static mapping = {
      templateNodes cascade: "all-delete-orphan" // delete nodes when opti is deleted
   }
}
