from django.contrib import admin

from derp_app.models import *


# Register your models here.
admin.site.register(User)
admin.site.register(Friend)
admin.site.register(Picture)
admin.site.register(Comment)
admin.site.register(Vote)
admin.site.register(Notification)