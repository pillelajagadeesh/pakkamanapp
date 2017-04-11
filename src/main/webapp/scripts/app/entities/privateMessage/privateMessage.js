'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('privateMessage', {
                parent: 'entity',
                url: '/privateMessages',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.privateMessage.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessages.html',
                        controller: 'PrivateMessageController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('privateMessage');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('privateMessage.detail', {
                parent: 'entity',
                url: '/privateMessage/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.privateMessage.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-detail.html',
                        controller: 'PrivateMessageDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('privateMessage');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'PrivateMessage', function($stateParams, PrivateMessage) {
                        return PrivateMessage.get({id : $stateParams.id});
                    }]
                }
            })
            .state('privateMessage.new', {
                parent: 'privateMessage',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-dialog.html',
                        controller: 'PrivateMessageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    message: null,
                                    read: null,
                                    delivered: null,
                                    created: null,
                                    notificationId: null,
                                    senderId: null,
                                    receiverId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('privateMessage');
                    })
                }]
            })
            .state('privateMessage.edit', {
                parent: 'privateMessage',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-dialog.html',
                        controller: 'PrivateMessageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['PrivateMessage', function(PrivateMessage) {
                                return PrivateMessage.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('privateMessage.delete', {
                parent: 'privateMessage',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-delete-dialog.html',
                        controller: 'PrivateMessageDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['PrivateMessage', function(PrivateMessage) {
                                return PrivateMessage.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
