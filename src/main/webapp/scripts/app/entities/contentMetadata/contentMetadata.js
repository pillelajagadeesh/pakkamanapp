'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('contentMetadata', {
                parent: 'admin',
                url: '/contentMetadatas',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.contentMetadata.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/contentMetadata/contentMetadatas.html',
                        controller: 'ContentMetadataController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('contentMetadata');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('contentMetadata.detail', {
                parent: 'entity',
                url: '/contentMetadata/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.contentMetadata.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/contentMetadata/contentMetadata-detail.html',
                        controller: 'ContentMetadataDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('contentMetadata');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ContentMetadata', function($stateParams, ContentMetadata) {
                        return ContentMetadata.get({id : $stateParams.id});
                    }]
                }
            })
            .state('contentMetadata.new', {
                parent: 'contentMetadata',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/contentMetadata/contentMetadata-dialog.html',
                        controller: 'ContentMetadataDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    signature: null,
                                    format: null,
                                    resourceType: null,
                                    secureUrl: null,
                                    created: null,
                                    type: null,
                                    version: null,
                                    url: null,
                                    publicId: null,
                                    tags: null,
                                    orginalFileName: null,
                                    bytes: null,
                                    width: null,
                                    eTag: null,
                                    height: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('contentMetadata', null, { reload: true });
                    }, function() {
                        $state.go('contentMetadata');
                    })
                }]
            })
            .state('contentMetadata.edit', {
                parent: 'contentMetadata',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/contentMetadata/contentMetadata-dialog.html',
                        controller: 'ContentMetadataDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ContentMetadata', function(ContentMetadata) {
                                return ContentMetadata.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('contentMetadata', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('contentMetadata.delete', {
                parent: 'contentMetadata',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/contentMetadata/contentMetadata-delete-dialog.html',
                        controller: 'ContentMetadataDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ContentMetadata', function(ContentMetadata) {
                                return ContentMetadata.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('contentMetadata', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
