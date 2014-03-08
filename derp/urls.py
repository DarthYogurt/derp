from django.conf.urls import patterns, include, url
from django.contrib import admin

from derp import settings
from derp_app.views import *


admin.autodiscover()
urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'derp.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^login/', login),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^uploadPic/', uploadPic),
    url(r'^getPic/', getPic),
    url(r'^getTeamGallery/(\d*)/$', getTeamGallery),
    #url(r'^getRandomPic/(\d*)/$', getRandomPic),
    url(r'^getUserId/(\d*)/$', getUserId),
    url(r'^gallery/(\d*)/(\d*)/$', gallery),
    url(r'^external/(\d*)/$', externalPicView),
    url(r'^vote/', vote),
    url(r'^getFriends/', getFriends),
    
    url(r'^getNotification/', getNotification),
    url(r'^signup/', signup),
    url(r'^bugreport/', bugReport),


    url(r'^uploadError/$', uploadError),
    url(r'^latestError/$', latestError),
    
    url(r'^addComment', addComment),
    url(r'^derp_media/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.MEDIA_ROOT, 'show_indexes': False}),
)
