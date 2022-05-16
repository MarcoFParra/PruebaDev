package fp.marco.pruebadev.ui.notifications

import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import fp.marco.pruebadev.databinding.FragmentNotificationsBinding
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    var storageRef = FirebaseStorage.getInstance().reference
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnImages.setOnClickListener {
            openGalleryForImages()
        }
    }
        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val REQUEST_CODE = 200

    private fun openGalleryForImages() {
        var intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Choose Pictures")
            , REQUEST_CODE
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){

            // if multiple images are selected
            if (data?.getClipData() != null) {
                var count = data.clipData!!.itemCount

                uploadImages(0,data.clipData!!, null)


            } else if (data?.getData() != null) {
                // if single image is selected
                val clipData: ClipData
                var imageUri: Uri = data.data!!
                uploadImages(0,null, imageUri)

                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview

            }
        }
    }

    private fun uploadImages(i: Int, data: ClipData?, fl:Uri?) {

        var file: Uri? = null
        if (data != null)
        if (i >= data.itemCount) {
           completeload()
            return
        }
        if (data != null)
         file = data.getItemAt(i).uri
        else if (fl != null)
            file = fl

        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        val uploadTask = storageRef.child("images/${file!!.lastPathSegment}").putFile(file, metadata)

        uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            binding.progress.progress = progress.toInt()
            Log.d("FireStorage", "Upload is $progress% done")
        }.addOnPausedListener {
            Log.d("FireStorage", "Upload is paused")
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(requireContext(), "la imagen no se pudo agregar a cloud storage", Toast.LENGTH_SHORT).show();
            binding.tvProgress.setText("Ocurrio un error al cargar las imagenes")
            binding.btnImages.isEnabled = true
            binding.imageView.setImageResource(0);
            binding.progress.progress=0

        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "Image agregada a cloud storage", Toast.LENGTH_SHORT).show();
            if (data != null){
            uploadImages(i+1, data,fl)
            }
            else{
                completeload()
            }
        }

        binding.imageView.setImageURI(file)
        if (data != null)
            binding.tvProgress.setText("Subiendo imagen ${i+1} de ${data.itemCount}")
        else
            binding.tvProgress.setText("Subiendo imagen 1 de 1")

        binding.btnImages.isEnabled = false


    }

    private fun completeload()
    {
        binding.tvProgress.setText("Imagen(es) cargada(s) con exito")
        binding.btnImages.isEnabled = true
        binding.imageView.setImageResource(0);
        binding.progress.progress=0
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }



}