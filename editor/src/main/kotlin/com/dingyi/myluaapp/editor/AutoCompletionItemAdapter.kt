package com.dingyi.myluaapp.editor

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dingyi.myluaapp.editor.kts.dp
import com.dingyi.myluaapp.editor.data.ColorCompletionItem
import io.github.rosemoe.sora.widget.EditorCompletionAdapter


/**
 * @author: dingyi
 * @date: 2021/8/15 6:36
 * @description:
 **/
class AutoCompletionItemAdapter(private val codeEditor: io.github.rosemoe.sora.widget.CodeEditor) :
    EditorCompletionAdapter() {


    class ViewHolder(val itemView: View) {
        val title: TextView = itemView.findViewById(R.id.result_item_label)
        val image: ImageView = itemView.findViewById(R.id.result_item_image)
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        isCurrentCursorPosition: Boolean
    ): View {

        var rootView = convertView

        if (rootView == null) {
            rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.editor_auto_completion_item, parent, false)
        }
        rootView?.let {
            if (it.tag == null) {
                it.tag = ViewHolder(it)
            }

            (it.tag as ViewHolder).apply {
                if (isCurrentCursorPosition)
                    itemView.setBackgroundColor(0xffdddddd.toInt());
                else
                    itemView.setBackgroundColor(0xffffffff.toInt());


                val spannableStringBuilder = SpannableStringBuilder()
                    .append((getItem(position) as ColorCompletionItem).colorLabel)

                val startIndex = spannableStringBuilder.length
                spannableStringBuilder.append(
                    " :" + getItem(position).desc,
                    ForegroundColorSpan(0xff808080.toInt()),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableStringBuilder.setSpan(
                    AbsoluteSizeSpan(14, true),
                    startIndex, spannableStringBuilder.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                title.typeface = codeEditor.typefaceText

                title.text = spannableStringBuilder

                if (getItem(position).commit.indexOf("(") != -1) {
                    getItem(position).cursorOffset(getItem(position).commit.indexOf("(") + 1)
                } else {
                    getItem(position).cursorOffset(getItem(position).commit.length)
                }
                image.setImageDrawable(getItem(position).icon)
            }
        }

        return rootView ?: View(parent.context)
    }

    override fun getItemHeight(): Int {
        return 32.dp
    }

}