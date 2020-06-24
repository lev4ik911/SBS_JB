package by.iba.sbs.library.service


import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

class SharedResources {
    companion object {
        fun getSharedString(stringRes: StringResource): StringDesc  {
            return StringDesc.Resource(stringRes)
        }
        fun getSharedString(stringRes: StringResource, input: String): StringDesc  {
            return StringDesc.ResourceFormatted(stringRes, input)
        }
    }
}