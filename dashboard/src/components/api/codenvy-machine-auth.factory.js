/*
 *  [2015] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
'use strict';

export class CodenvyMachineAuth {

         /**
          * Default constructor that is using resource
          * @ngInject for Dependency injection
          */
         constructor($injector, $q) {
           this.$injector = $window;
           this.$q = $q;
           this.tokens = [];
         }

         requestToken(workspaceId) {
           return this.$injector.get('$http').get('/api/machine/token/' + workspaceId)
           .then((resp) => this.tokens[workspaceId] = resp.data.machineToken);
         }

         getWorkspaceId(url) {
           // examples:
           // ws-agent-host/wsagent/api/ext/project/:workspaceId/import/:path
           // ws-agent-host/wsagent/api/ext/project-type/:workspaceId
           // ws-agent-host/wsagent/api/ext/git/:workspaceId/read-only-url?projectPath=:path'
           var groups = /.+\/ext\/(project|project-type|git)\/(.+?(?=\/)|.+).*/.exec(url);
           return groups ? groups[2] : undefined;
         }

         request(config) {
           if (config.url.indexOf("/ext/") === -1) {
             return config || this.$q.when(config);
           }

           let workspaceId = getWorkspaceId(config.url);
           if (!workspaceId) {
             return config || this.$q.when(config);
           }

           return $q.when(this.tokens[workspaceId] || requestToken(workspaceId))
             .then((token) => {
             config.headers['Authorization'] = token;
           return config;
           })
         }

         responseError(rejection) {
           if (rejection && rejection.config.url.indexOf("/ext/") !== -1) {
             delete tokens[getWorkspaceId(rejection.config.url)];
           }
           return this.$q.reject(rejection);
         }
}
